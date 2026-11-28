package axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates;

import axiom.daos.tourmate.platform.tourmonitoring.domain.exceptions.TourExecutionDomainException;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Checkpoint;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.ParticipantStatus;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.CheckpointReached;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.IncidentReported;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.IncidentResolved;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.RiskLevelChanged;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.SosTriggered;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.TourAborted;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.TourCompleted;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.TourPaused;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.TourResumed;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.TourStarted;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionAlertType;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionIncidentSeverity;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.GeoLocation;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ParticipantAttendanceStatus;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.RiskLevel;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TimeWindow;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link TourExecution} state machine, business rules, and invariants.
 */
class TourExecutionTest {

    private Instant baseTime;
    private TourExecution execution;

    @BeforeEach
    void setUp() {
        baseTime = Instant.parse("2026-12-01T08:00:00Z");
        var cp1 = new Checkpoint(
                1L,
                "Plaza de Armas",
                1,
                new GeoLocation(-13.5167, -71.9788),
                new TimeWindow(baseTime.plus(30, ChronoUnit.MINUTES), baseTime.plus(45, ChronoUnit.MINUTES))
        );
        var cp2 = new Checkpoint(
                2L,
                "Sacsayhuaman",
                2,
                new GeoLocation(-13.5089, -71.9817),
                new TimeWindow(baseTime.plus(90, ChronoUnit.MINUTES), baseTime.plus(105, ChronoUnit.MINUTES))
        );
        var p1 = new ParticipantStatus(10L, 100L, 500L);
        var p2 = new ParticipantStatus(11L, 101L, 500L);

        execution = new TourExecution(
                1L,
                2L,
                500L,
                baseTime,
                new GeoLocation(-13.5170, -71.9785),
                List.of(cp2, cp1),
                List.of(p1, p2)
        );
    }

    @Test
    @DisplayName("Should initialize in SCHEDULED state with LOW risk and sorted checkpoints")
    void shouldInitializeInScheduledStateWithSortedCheckpoints() {
        assertEquals(TourStatus.SCHEDULED, execution.getStatus());
        assertEquals(RiskLevel.LOW, execution.getRiskLevel());
        assertEquals(1L, execution.getCheckpoints().get(0).getId());
        assertEquals(2L, execution.getCheckpoints().get(1).getId());
    }

    @Test
    @DisplayName("Should enforce valid lifecycle transitions SCHEDULED -> IN_PROGRESS -> PAUSED -> IN_PROGRESS -> COMPLETED")
    void shouldEnforceValidStateTransitions() {
        execution.checkInParticipant(10L, baseTime.minus(5, ChronoUnit.MINUTES));
        execution.start(true, 1, baseTime);
        assertEquals(TourStatus.IN_PROGRESS, execution.getStatus());
        assertTrue(execution.domainEvents().stream().anyMatch(TourStarted.class::isInstance));

        execution.pause("Heavy rain on trail", baseTime.plus(15, ChronoUnit.MINUTES));
        assertEquals(TourStatus.PAUSED, execution.getStatus());
        assertEquals("Heavy rain on trail", execution.getPauseReason());
        assertTrue(execution.domainEvents().stream().anyMatch(TourPaused.class::isInstance));

        execution.resume(baseTime.plus(25, ChronoUnit.MINUTES));
        assertEquals(TourStatus.IN_PROGRESS, execution.getStatus());
        assertTrue(execution.domainEvents().stream().anyMatch(TourResumed.class::isInstance));

        execution.complete(baseTime.plus(120, ChronoUnit.MINUTES));
        assertEquals(TourStatus.COMPLETED, execution.getStatus());
        assertEquals(120L, execution.getTotalDurationMinutes());
        assertEquals(ParticipantAttendanceStatus.ABSENT, execution.getParticipants().get(1).getStatus());
        assertTrue(execution.domainEvents().stream().anyMatch(TourCompleted.class::isInstance));
    }

    @Test
    @DisplayName("Should reject starting when check-ins are below minimum threshold or guide is disabled")
    void shouldRejectStartingWithInsufficientCheckInsOrDisabledGuide() {
        var disabledEx = assertThrows(
                TourExecutionDomainException.class,
                () -> execution.start(false, 1, baseTime)
        );
        assertEquals("GUIDE_NOT_ENABLED", disabledEx.getErrorCode());

        var insufficientEx = assertThrows(
                TourExecutionDomainException.class,
                () -> execution.start(true, 1, baseTime)
        );
        assertEquals("INSUFFICIENT_CHECK_INS", insufficientEx.getErrorCode());
    }

    @Test
    @DisplayName("Should reject invalid state transitions with INVALID_STATE_TRANSITION")
    void shouldRejectInvalidStateTransitions() {
        var reachBeforeStart = assertThrows(
                TourExecutionDomainException.class,
                () -> execution.reachCheckpoint(1L, baseTime.plus(30, ChronoUnit.MINUTES), 15)
        );
        assertEquals("INVALID_STATE_TRANSITION", reachBeforeStart.getErrorCode());

        execution.checkInParticipant(10L, baseTime);
        execution.start(true, 1, baseTime);

        var startTwice = assertThrows(
                TourExecutionDomainException.class,
                () -> execution.start(true, 1, baseTime.plus(1, ChronoUnit.MINUTES))
        );
        assertEquals("INVALID_STATE_TRANSITION", startTwice.getErrorCode());

        execution.pause("Rest stop", baseTime.plus(10, ChronoUnit.MINUTES));

        var completeWhilePaused = assertThrows(
                TourExecutionDomainException.class,
                () -> execution.complete(baseTime.plus(20, ChronoUnit.MINUTES))
        );
        assertEquals("INVALID_STATE_TRANSITION", completeWhilePaused.getErrorCode());
    }

    @Test
    @DisplayName("Should calculate checkpoint delay against TimeWindow and generate alert when exceeding tolerance")
    void shouldCalculateCheckpointDelayAndCreateAlertWhenExceedingTolerance() {
        execution.checkInParticipant(10L, baseTime);
        execution.start(true, 1, baseTime);

        // cp1 latestAllowedArrival is baseTime + 45m. Reaching at baseTime + 65m with 15m tolerance => 20m delay (> 15m)
        Instant reachedAt = baseTime.plus(65, ChronoUnit.MINUTES);
        var cp1 = execution.reachCheckpoint(1L, reachedAt, 15);

        assertTrue(cp1.isReached());
        assertEquals(35L, cp1.getDelayMinutes());
        assertTrue(cp1.isDelayedBeyondTolerance());
        assertEquals(1, execution.getAlerts().size());
        assertEquals(ExecutionAlertType.CHECKPOINT_DELAY, execution.getAlerts().get(0).getType());
        assertTrue(execution.domainEvents().stream().anyMatch(CheckpointReached.class::isInstance));

        // Duplicate reach must fail with CHECKPOINT_ALREADY_REACHED
        var duplicateEx = assertThrows(
                TourExecutionDomainException.class,
                () -> execution.reachCheckpoint(1L, reachedAt.plus(5, ChronoUnit.MINUTES), 15)
        );
        assertEquals("CHECKPOINT_ALREADY_REACHED", duplicateEx.getErrorCode());
    }

    @Test
    @DisplayName("Should escalate RiskLevel after N MEDIUM incidents or 1 CRITICAL incident and block completion until resolved")
    void shouldEscalateRiskLevelAndEnforceCriticalIncidentResolutionBeforeCompletion() {
        execution.checkInParticipant(10L, baseTime);
        execution.start(true, 1, baseTime);

        execution.reportIncident(
                "WEATHER",
                ExecutionIncidentSeverity.MEDIUM,
                "Slippery rocks on section 1",
                null,
                2L,
                "GUIDE",
                2,
                baseTime.plus(10, ChronoUnit.MINUTES)
        );
        assertEquals(RiskLevel.LOW, execution.getRiskLevel());

        execution.reportIncident(
                "LOGISTICS",
                ExecutionIncidentSeverity.MEDIUM,
                "Bus delayed at pickup point",
                null,
                2L,
                "GUIDE",
                2,
                baseTime.plus(15, ChronoUnit.MINUTES)
        );
        assertEquals(RiskLevel.MODERATE, execution.getRiskLevel());
        assertTrue(execution.domainEvents().stream().anyMatch(RiskLevelChanged.class::isInstance));

        var criticalIncident = execution.reportIncident(
                "MEDICAL",
                ExecutionIncidentSeverity.CRITICAL,
                "Severe dehydration requiring medical attention",
                null,
                100L,
                "TOURIST",
                2,
                baseTime.plus(20, ChronoUnit.MINUTES)
        );
        assertEquals(RiskLevel.CRITICAL, execution.getRiskLevel());
        assertTrue(execution.domainEvents().stream().anyMatch(IncidentReported.class::isInstance));

        // Cannot complete while critical incident is unresolved
        var unresolvedEx = assertThrows(
                TourExecutionDomainException.class,
                () -> execution.complete(baseTime.plus(50, ChronoUnit.MINUTES))
        );
        assertEquals("UNRESOLVED_CRITICAL_INCIDENTS", unresolvedEx.getErrorCode());

        // Resolve critical incident
        execution.resolveIncident(
                criticalIncident.getId(),
                "Paramedic team assisted participant and cleared for return",
                baseTime.plus(40, ChronoUnit.MINUTES)
        );
        assertTrue(execution.domainEvents().stream().anyMatch(IncidentResolved.class::isInstance));

        // Resolving twice must fail with INCIDENT_ALREADY_RESOLVED
        var resolveTwiceEx = assertThrows(
                TourExecutionDomainException.class,
                () -> execution.resolveIncident(criticalIncident.getId(), "Duplicate note", baseTime.plus(45, ChronoUnit.MINUTES))
        );
        assertEquals("INCIDENT_ALREADY_RESOLVED", resolveTwiceEx.getErrorCode());

        // Now completion succeeds
        execution.complete(baseTime.plus(60, ChronoUnit.MINUTES));
        assertEquals(TourStatus.COMPLETED, execution.getStatus());
    }

    @Test
    @DisplayName("Should trigger SOS alert idempotently per active source user and allow abort with mandatory reason")
    void shouldTriggerSosIdempotentlyAndAllowAbort() {
        execution.checkInParticipant(10L, baseTime);
        execution.start(true, 1, baseTime);

        var firstSos = execution.triggerSos(
                100L,
                "TOURIST",
                "Lost visual contact with group",
                new GeoLocation(-13.5100, -71.9800),
                baseTime.plus(30, ChronoUnit.MINUTES)
        );
        assertNotNull(firstSos);
        assertEquals(ExecutionAlertType.SOS, firstSos.getType());
        assertEquals(RiskLevel.CRITICAL, execution.getRiskLevel());
        assertTrue(execution.domainEvents().stream().anyMatch(SosTriggered.class::isInstance));

        // Second SOS from same user while active returns the same alert without adding a duplicate
        var secondSos = execution.triggerSos(
                100L,
                "TOURIST",
                "Still waiting at trail marker",
                new GeoLocation(-13.5100, -71.9800),
                baseTime.plus(32, ChronoUnit.MINUTES)
        );
        assertEquals(firstSos.getId(), secondSos.getId());
        long sosAlertsCount = execution.getAlerts().stream()
                .filter(a -> a.getType() == ExecutionAlertType.SOS)
                .count();
        assertEquals(1L, sosAlertsCount);

        // Abort deactivates active alerts and transitions to ABORTED
        execution.abort("Emergency evacuation initiated", baseTime.plus(40, ChronoUnit.MINUTES));
        assertEquals(TourStatus.ABORTED, execution.getStatus());
        assertFalse(firstSos.isActive());
        assertTrue(execution.domainEvents().stream().anyMatch(TourAborted.class::isInstance));
    }
}
