package axiom.daos.tourmate.platform.tourmonitoring.application.internal.commandservices;

import axiom.daos.tourmate.platform.iam.interfaces.acl.IamContextFacade;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.tourmonitoring.application.internal.security.TourExecutionAccessPolicy;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.TourExecution;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.CreateTourExecutionCommand;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Checkpoint;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.ParticipantStatus;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionIncidentSeverity;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.GeoLocation;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TimeWindow;
import axiom.daos.tourmate.platform.tourmonitoring.domain.repositories.TourExecutionRepository;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.configuration.TourMonitoringProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TourExecutionCommandServiceImpl}, verifying RBAC/ownership anti-IDOR rules and optimistic locking.
 */
@ExtendWith(MockitoExtension.class)
class TourExecutionCommandServiceImplTest {

    @Mock
    private TourExecutionRepository tourExecutionRepository;

    @Mock
    private IamContextFacade iamContextFacade;

    private TourExecutionCommandServiceImpl commandService;
    private TourExecution sampleExecution;

    @BeforeEach
    void setUp() {
        var properties = new TourMonitoringProperties();
        properties.setMinCheckIns(1);
        properties.setMediumIncidentsThreshold(2);
        properties.setCheckpointDelayToleranceMinutes(15);

        var accessPolicy = new TourExecutionAccessPolicy();
        commandService = new TourExecutionCommandServiceImpl(
                tourExecutionRepository,
                properties,
                accessPolicy,
                iamContextFacade
        );

        Instant now = Instant.parse("2026-12-01T08:00:00Z");
        sampleExecution = new TourExecution(
                10L,
                2L, // Assigned guideId = 2
                100L,
                now,
                new GeoLocation(-13.5167, -71.9788),
                List.of(new Checkpoint(
                        1L,
                        "Checkpoint 1",
                        1,
                        new GeoLocation(-13.5160, -71.9780),
                        new TimeWindow(now.plus(30, ChronoUnit.MINUTES), now.plus(45, ChronoUnit.MINUTES))
                )),
                List.of(new ParticipantStatus(10L, 3L, 100L)) // Enrolled touristId = 3
        );
    }

    @Test
    @DisplayName("Should forbid another guide from checking in or starting a tour they do not own (anti-IDOR)")
    void shouldForbidNonOwnerGuideFromOperatingTour() {
        when(tourExecutionRepository.findById(1L)).thenReturn(Optional.of(sampleExecution));

        var command = new CreateTourExecutionCommand.CheckInParticipantCommand(
                1L,
                10L,
                99L, // Different guide ID
                Set.of("ROLE_TOUR_GUIDE")
        );

        var result = commandService.handle(command);

        var failure = assertInstanceOf(Result.Failure.class, result);
        var error = (ApplicationError) failure.error();
        assertEquals("FORBIDDEN", error.code());
        verify(tourExecutionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should forbid an unrelated tourist from reporting an incident or SOS on a tour they are not enrolled in")
    void shouldForbidUnenrolledTouristFromReportingIncident() {
        sampleExecution.checkInParticipant(10L, Instant.now());
        sampleExecution.start(true, 1, Instant.now());
        when(tourExecutionRepository.findById(1L)).thenReturn(Optional.of(sampleExecution));

        var command = new CreateTourExecutionCommand.ReportIncidentCommand(
                1L,
                "SAFETY",
                ExecutionIncidentSeverity.MEDIUM,
                "Unauthorized tourist trying to report",
                null,
                null,
                777L, // Not enrolled (enrolled tourist is 3L)
                Set.of("ROLE_USER")
        );

        var result = commandService.handle(command);

        var failure = assertInstanceOf(Result.Failure.class, result);
        var error = (ApplicationError) failure.error();
        assertEquals("FORBIDDEN", error.code());
        verify(tourExecutionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should allow enrolled tourist to report an incident and assigned guide to start tour")
    void shouldAllowAssignedGuideAndEnrolledTourist() {
        when(tourExecutionRepository.findById(1L)).thenReturn(Optional.of(sampleExecution));
        when(tourExecutionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var checkInResult = commandService.handle(new CreateTourExecutionCommand.CheckInParticipantCommand(
                1L,
                10L,
                2L,
                Set.of("ROLE_TOUR_GUIDE")
        ));
        assertTrue(checkInResult.isSuccess());

        var startResult = commandService.handle(new CreateTourExecutionCommand.StartTourExecutionCommand(
                1L,
                2L,
                Set.of("ROLE_TOUR_GUIDE")
        ));
        assertTrue(startResult.isSuccess());

        var incidentResult = commandService.handle(new CreateTourExecutionCommand.ReportIncidentCommand(
                1L,
                "MEDICAL",
                ExecutionIncidentSeverity.LOW,
                "Minor blister",
                -13.5165,
                -71.9782,
                3L, // Enrolled touristId = 3L
                Set.of("ROLE_USER")
        ));
        assertTrue(incidentResult.isSuccess());
    }

    @Test
    @DisplayName("Should map ObjectOptimisticLockingFailureException to CONCURRENT_MODIFICATION_CONFLICT (409)")
    void shouldMapOptimisticLockingFailureToStateConflict() {
        sampleExecution.checkInParticipant(10L, Instant.now());
        sampleExecution.start(true, 1, Instant.now());
        when(tourExecutionRepository.findById(1L)).thenReturn(Optional.of(sampleExecution));
        when(tourExecutionRepository.save(any()))
                .thenThrow(new ObjectOptimisticLockingFailureException(TourExecution.class, 1L));

        var pauseCommand = new CreateTourExecutionCommand.PauseTourExecutionCommand(
                1L,
                "Weather pause",
                2L,
                Set.of("ROLE_TOUR_GUIDE")
        );

        var result = commandService.handle(pauseCommand);

        var failure = assertInstanceOf(Result.Failure.class, result);
        var error = (ApplicationError) failure.error();
        assertEquals("CONCURRENT_MODIFICATION_CONFLICT", error.code());
    }
}
