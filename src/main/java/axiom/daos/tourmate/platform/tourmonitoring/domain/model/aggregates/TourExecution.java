package axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates;

import axiom.daos.tourmate.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import axiom.daos.tourmate.platform.tourmonitoring.domain.exceptions.TourExecutionDomainException;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Alert;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Checkpoint;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Incident;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.ParticipantStatus;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.TimelineEntry;
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
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.RiskLevel;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourStatus;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Aggregate root representing a concrete tour departure execution in real time.
 *
 * <p>Enforces the lifecycle state machine ({@code SCHEDULED -> IN_PROGRESS <-> PAUSED -> COMPLETED | ABORTED}),
 * participant check-in invariants, checkpoint delay detection, incident risk escalation, and SOS idempotency.</p>
 */
@Getter
public class TourExecution extends AbstractDomainAggregateRoot<TourExecution> {

    private Long id;
    private Long version;
    private final Long tourId;
    private final Long guideId;
    private final Long bookingId;
    private TourStatus status;
    private RiskLevel riskLevel;
    private GeoLocation currentLocation;
    private final Instant scheduledStartAt;
    private Instant startedAt;
    private Instant pausedAt;
    private Instant completedAt;
    private Instant abortedAt;
    private String pauseReason;
    private String abortReason;

    private final List<Checkpoint> checkpoints;
    private final List<ParticipantStatus> participants;
    private final List<Incident> incidents;
    private final List<Alert> alerts;
    private final List<TimelineEntry> timeline;

    public TourExecution(
            Long tourId,
            Long guideId,
            Long bookingId,
            Instant scheduledStartAt,
            GeoLocation initialLocation,
            List<Checkpoint> checkpoints,
            List<ParticipantStatus> participants
    ) {
        if (tourId == null || tourId < 1) {
            throw new IllegalArgumentException("Tour ID cannot be null or less than 1");
        }
        if (guideId == null || guideId < 1) {
            throw new IllegalArgumentException("Guide ID cannot be null or less than 1");
        }
        this.tourId = tourId;
        this.guideId = guideId;
        this.bookingId = bookingId;
        this.scheduledStartAt = Objects.requireNonNull(scheduledStartAt, "Scheduled start time is required");
        this.currentLocation = initialLocation;
        this.status = TourStatus.SCHEDULED;
        this.riskLevel = RiskLevel.LOW;
        this.checkpoints = new ArrayList<>(checkpoints != null ? checkpoints : List.of());
        this.checkpoints.sort(Comparator.comparingInt(Checkpoint::getSequenceOrder));
        this.participants = new ArrayList<>(participants != null ? participants : List.of());
        this.incidents = new ArrayList<>();
        this.alerts = new ArrayList<>();
        this.timeline = new ArrayList<>();
    }

    public static TourExecution reconstitute(
            Long id,
            Long version,
            Long tourId,
            Long guideId,
            Long bookingId,
            TourStatus status,
            RiskLevel riskLevel,
            GeoLocation currentLocation,
            Instant scheduledStartAt,
            Instant startedAt,
            Instant pausedAt,
            Instant completedAt,
            Instant abortedAt,
            String pauseReason,
            String abortReason,
            List<Checkpoint> checkpoints,
            List<ParticipantStatus> participants,
            List<Incident> incidents,
            List<Alert> alerts,
            List<TimelineEntry> timeline
    ) {
        var execution = new TourExecution(
                tourId,
                guideId,
                bookingId,
                scheduledStartAt,
                currentLocation,
                checkpoints,
                participants
        );
        execution.id = id;
        execution.version = version;
        execution.status = Objects.requireNonNull(status, "Status is required");
        execution.riskLevel = Objects.requireNonNull(riskLevel, "Risk level is required");
        execution.startedAt = startedAt;
        execution.pausedAt = pausedAt;
        execution.completedAt = completedAt;
        execution.abortedAt = abortedAt;
        execution.pauseReason = pauseReason;
        execution.abortReason = abortReason;
        if (incidents != null) {
            execution.incidents.addAll(incidents);
        }
        if (alerts != null) {
            execution.alerts.addAll(alerts);
        }
        if (timeline != null) {
            execution.timeline.addAll(timeline);
        }
        return execution;
    }

    /**
     * Checks in a participant before or during the tour execution.
     */
    public ParticipantStatus checkInParticipant(Long participantId, Instant checkedInAt) {
        if (this.status.isTerminal()) {
            throw TourExecutionDomainException.conflict(
                    "INVALID_STATE_TRANSITION",
                    "Cannot check in participants when tour execution is in status " + this.status
            );
        }
        var participant = this.participants.stream()
                .filter(p -> p.getParticipantId().equals(participantId) || p.getTouristId().equals(participantId))
                .findFirst()
                .orElseThrow(() -> TourExecutionDomainException.notFound("Participant", String.valueOf(participantId)));

        participant.checkIn(checkedInAt);
        recordTimeline(
                "PARTICIPANT_CHECKED_IN",
                "Participant #%d checked in".formatted(participant.getParticipantId()),
                checkedInAt
        );
        return participant;
    }

    /**
     * Starts the tour execution from {@code SCHEDULED}.
     */
    public void start(boolean guideEnabled, int minRequiredCheckIns, Instant startedAt) {
        Objects.requireNonNull(startedAt, "Start timestamp is required");
        if (this.status != TourStatus.SCHEDULED) {
            throw TourExecutionDomainException.conflict(
                    "INVALID_STATE_TRANSITION",
                    "Tour execution can only be started from SCHEDULED state, current state: " + this.status
            );
        }
        if (!guideEnabled) {
            throw TourExecutionDomainException.businessRule(
                    "GUIDE_NOT_ENABLED",
                    "Assigned guide #%d is not enabled to start the tour".formatted(this.guideId)
            );
        }
        long checkedInCount = getCheckedInCount();
        if (checkedInCount < minRequiredCheckIns) {
            throw TourExecutionDomainException.businessRule(
                    "INSUFFICIENT_CHECK_INS",
                    "At least %d checked-in participants are required to start (current: %d)"
                            .formatted(minRequiredCheckIns, checkedInCount)
            );
        }

        this.status = TourStatus.IN_PROGRESS;
        this.startedAt = startedAt;
        recordTimeline("TOUR_STARTED", "Tour execution started by guide #" + this.guideId, startedAt);
        registerDomainEvent(new TourStarted(this.id, this.tourId, this.guideId, startedAt));
    }

    /**
     * Marks a checkpoint as reached while {@code IN_PROGRESS} and evaluates delay against its {@code TimeWindow}.
     */
    public Checkpoint reachCheckpoint(Long checkpointId, Instant reachedAt, long delayToleranceMinutes) {
        Objects.requireNonNull(reachedAt, "Reached timestamp is required");
        if (this.status != TourStatus.IN_PROGRESS) {
            throw TourExecutionDomainException.conflict(
                    "INVALID_STATE_TRANSITION",
                    "Checkpoints can only be reached while tour is IN_PROGRESS, current state: " + this.status
            );
        }
        var checkpoint = this.checkpoints.stream()
                .filter(cp -> cp.getId().equals(checkpointId))
                .findFirst()
                .orElseThrow(() -> TourExecutionDomainException.notFound("Checkpoint", String.valueOf(checkpointId)));

        checkpoint.markReached(reachedAt, delayToleranceMinutes);
        this.currentLocation = checkpoint.getLocation();

        recordTimeline(
                "CHECKPOINT_REACHED",
                "Checkpoint '%s' reached (delay: %d min)".formatted(checkpoint.getName(), checkpoint.getDelayMinutes()),
                reachedAt
        );
        registerDomainEvent(new CheckpointReached(
                this.id,
                checkpoint.getId(),
                checkpoint.getName(),
                reachedAt,
                checkpoint.getDelayMinutes(),
                checkpoint.isDelayedBeyondTolerance()
        ));

        if (checkpoint.isDelayedBeyondTolerance()) {
            addAlert(new Alert(
                    nextAlertId(),
                    ExecutionAlertType.CHECKPOINT_DELAY,
                    this.riskLevel.max(RiskLevel.MODERATE),
                    "Delay of %d minutes detected at checkpoint '%s'".formatted(
                            checkpoint.getDelayMinutes(),
                            checkpoint.getName()
                    ),
                    this.guideId,
                    "GUIDE",
                    checkpoint.getLocation(),
                    true,
                    reachedAt
            ));
        }
        return checkpoint;
    }

    /**
     * Reports an incident during the tour execution and evaluates automatic {@code RiskLevel} escalation.
     */
    public Incident reportIncident(
            String type,
            ExecutionIncidentSeverity severity,
            String description,
            GeoLocation location,
            Long reportedById,
            String reportedByRole,
            int mediumIncidentsThreshold,
            Instant reportedAt
    ) {
        if (this.status != TourStatus.IN_PROGRESS && this.status != TourStatus.PAUSED) {
            throw TourExecutionDomainException.conflict(
                    "INVALID_STATE_TRANSITION",
                    "Incidents can only be reported when tour is IN_PROGRESS or PAUSED, current state: " + this.status
            );
        }

        Long incidentId = (long) (this.incidents.size() + 1);
        var incident = new Incident(
                incidentId,
                type,
                severity,
                description,
                location != null ? location : this.currentLocation,
                reportedById,
                reportedByRole,
                reportedAt
        );
        this.incidents.add(incident);

        recordTimeline(
                "INCIDENT_REPORTED",
                "Incident reported [%s - %s]: %s".formatted(type, severity, description),
                reportedAt
        );
        registerDomainEvent(new IncidentReported(
                this.id,
                incident.getId(),
                incident.getType(),
                incident.getSeverity(),
                incident.getReportedById(),
                reportedAt
        ));

        evaluateRiskEscalationAfterIncident(incident, mediumIncidentsThreshold, reportedAt);
        return incident;
    }

    private void evaluateRiskEscalationAfterIncident(
            Incident incident,
            int mediumIncidentsThreshold,
            Instant occurredAt
    ) {
        RiskLevel previous = this.riskLevel;
        RiskLevel target = previous;
        String reason = null;

        if (incident.getSeverity() == ExecutionIncidentSeverity.CRITICAL) {
            target = RiskLevel.CRITICAL;
            reason = "Critical incident reported: " + incident.getType();
        } else if (incident.getSeverity() == ExecutionIncidentSeverity.HIGH) {
            target = previous.escalate().max(RiskLevel.HIGH);
            reason = "High severity incident reported: " + incident.getType();
        } else if (incident.getSeverity() == ExecutionIncidentSeverity.MEDIUM) {
            long mediumOrHigherOpenCount = this.incidents.stream()
                    .filter(Incident::isOpen)
                    .filter(i -> i.getSeverity() == ExecutionIncidentSeverity.MEDIUM
                            || i.getSeverity() == ExecutionIncidentSeverity.HIGH)
                    .count();
            int threshold = Math.max(1, mediumIncidentsThreshold);
            if (mediumOrHigherOpenCount >= threshold) {
                target = previous.escalate();
                reason = "Reached threshold of %d medium/high incidents".formatted(threshold);
            }
        }

        if (target.ordinal() > previous.ordinal() && reason != null) {
            this.riskLevel = target;
            recordTimeline(
                    "RISK_LEVEL_CHANGED",
                    "Risk level escalated from %s to %s (%s)".formatted(previous, target, reason),
                    occurredAt
            );
            registerDomainEvent(new RiskLevelChanged(this.id, previous, target, reason, occurredAt));
            addAlert(new Alert(
                    nextAlertId(),
                    ExecutionAlertType.RISK_ESCALATION,
                    target,
                    "Risk escalated to %s: %s".formatted(target, reason),
                    incident.getReportedById(),
                    incident.getReportedByRole(),
                    incident.getLocation(),
                    true,
                    occurredAt
            ));
        }
    }

    /**
     * Resolves an open incident with mandatory resolution notes.
     */
    public Incident resolveIncident(Long incidentId, String resolutionNotes, Instant resolvedAt) {
        var incident = this.incidents.stream()
                .filter(i -> Objects.equals(i.getId(), incidentId))
                .findFirst()
                .orElseThrow(() -> TourExecutionDomainException.notFound("Incident", String.valueOf(incidentId)));

        incident.resolve(resolutionNotes, resolvedAt);
        recordTimeline(
                "INCIDENT_RESOLVED",
                "Incident #%d resolved: %s".formatted(incidentId, incident.getResolutionNotes()),
                resolvedAt
        );
        registerDomainEvent(new IncidentResolved(this.id, incidentId, incident.getResolutionNotes(), resolvedAt));
        return incident;
    }

    /**
     * Triggers a critical SOS alert for administrators. Idempotent if an active SOS from the same user already exists.
     */
    public Alert triggerSos(
            Long sourceUserId,
            String sourceRole,
            String reason,
            GeoLocation location,
            Instant occurredAt
    ) {
        if (this.status != TourStatus.IN_PROGRESS && this.status != TourStatus.PAUSED) {
            throw TourExecutionDomainException.conflict(
                    "INVALID_STATE_TRANSITION",
                    "SOS can only be triggered when tour is IN_PROGRESS or PAUSED, current state: " + this.status
            );
        }
        if (sourceUserId == null || sourceUserId < 1) {
            throw new IllegalArgumentException("SOS source user ID is required");
        }

        Optional<Alert> existingActiveSos = this.alerts.stream()
                .filter(Alert::isActive)
                .filter(a -> a.getType() == ExecutionAlertType.SOS)
                .filter(a -> sourceUserId.equals(a.getSourceUserId()))
                .findFirst();

        if (existingActiveSos.isPresent()) {
            return existingActiveSos.get();
        }

        String sosMessage = (reason != null && !reason.isBlank())
                ? "SOS emergency triggered by %s #%d: %s".formatted(sourceRole, sourceUserId, reason.trim())
                : "SOS emergency triggered by %s #%d".formatted(sourceRole, sourceUserId);

        GeoLocation effectiveLocation = location != null ? location : this.currentLocation;
        var alert = new Alert(
                nextAlertId(),
                ExecutionAlertType.SOS,
                RiskLevel.CRITICAL,
                sosMessage,
                sourceUserId,
                sourceRole != null ? sourceRole : "GUIDE",
                effectiveLocation,
                true,
                occurredAt
        );
        addAlert(alert);

        if (this.riskLevel != RiskLevel.CRITICAL) {
            RiskLevel previous = this.riskLevel;
            this.riskLevel = RiskLevel.CRITICAL;
            recordTimeline(
                    "RISK_LEVEL_CHANGED",
                    "Risk level escalated from %s to CRITICAL due to SOS".formatted(previous),
                    occurredAt
            );
            registerDomainEvent(new RiskLevelChanged(
                    this.id,
                    previous,
                    RiskLevel.CRITICAL,
                    "Emergency SOS triggered",
                    occurredAt
            ));
        }

        recordTimeline("SOS_TRIGGERED", sosMessage, occurredAt);
        registerDomainEvent(new SosTriggered(
                this.id,
                alert.getId(),
                sourceUserId,
                alert.getSourceRole(),
                sosMessage,
                effectiveLocation,
                occurredAt
        ));
        return alert;
    }

    /**
     * Pauses an in-progress tour execution with a mandatory reason.
     */
    public void pause(String reason, Instant pausedAt) {
        if (!this.status.canTransitionTo(TourStatus.PAUSED)) {
            throw TourExecutionDomainException.conflict(
                    "INVALID_STATE_TRANSITION",
                    "Cannot pause tour execution from status " + this.status
            );
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Pause reason is required");
        }
        this.status = TourStatus.PAUSED;
        this.pauseReason = reason.trim();
        this.pausedAt = Objects.requireNonNull(pausedAt, "Paused timestamp is required");

        recordTimeline("TOUR_PAUSED", "Tour paused: " + this.pauseReason, pausedAt);
        registerDomainEvent(new TourPaused(this.id, this.pauseReason, pausedAt));
    }

    /**
     * Resumes a paused tour execution back to {@code IN_PROGRESS}.
     */
    public void resume(Instant resumedAt) {
        if (this.status != TourStatus.PAUSED) {
            throw TourExecutionDomainException.conflict(
                    "INVALID_STATE_TRANSITION",
                    "Can only resume a PAUSED tour execution, current state: " + this.status
            );
        }
        Objects.requireNonNull(resumedAt, "Resumed timestamp is required");
        this.status = TourStatus.IN_PROGRESS;
        recordTimeline("TOUR_RESUMED", "Tour execution resumed", resumedAt);
        registerDomainEvent(new TourResumed(this.id, resumedAt));
    }

    /**
     * Completes an in-progress tour execution provided no critical incidents remain unresolved.
     */
    public void complete(Instant completedAt) {
        if (this.status != TourStatus.IN_PROGRESS) {
            throw TourExecutionDomainException.conflict(
                    "INVALID_STATE_TRANSITION",
                    "Tour execution can only be completed from IN_PROGRESS state, current state: " + this.status
            );
        }
        boolean hasUnresolvedCritical = this.incidents.stream().anyMatch(Incident::isUnresolvedCritical);
        if (hasUnresolvedCritical) {
            throw TourExecutionDomainException.businessRule(
                    "UNRESOLVED_CRITICAL_INCIDENTS",
                    "Cannot complete tour execution while critical incidents remain unresolved"
            );
        }
        Objects.requireNonNull(completedAt, "Completed timestamp is required");
        this.status = TourStatus.COMPLETED;
        this.completedAt = completedAt;
        this.participants.forEach(ParticipantStatus::markAbsentIfPending);
        this.alerts.forEach(Alert::deactivate);

        long durationMinutes = getTotalDurationMinutes();
        int reachedCount = (int) this.checkpoints.stream().filter(Checkpoint::isReached).count();
        recordTimeline(
                "TOUR_COMPLETED",
                "Tour execution completed (duration: %d min, checkpoints: %d/%d)"
                        .formatted(durationMinutes, reachedCount, this.checkpoints.size()),
                completedAt
        );
        registerDomainEvent(new TourCompleted(
                this.id,
                completedAt,
                durationMinutes,
                reachedCount,
                this.incidents.size()
        ));
    }

    /**
     * Aborts the tour execution with a mandatory reason (terminal state).
     */
    public void abort(String reason, Instant abortedAt) {
        if (!this.status.canTransitionTo(TourStatus.ABORTED)) {
            throw TourExecutionDomainException.conflict(
                    "INVALID_STATE_TRANSITION",
                    "Cannot abort tour execution from terminal status " + this.status
            );
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Abort reason is required");
        }
        Objects.requireNonNull(abortedAt, "Aborted timestamp is required");
        this.status = TourStatus.ABORTED;
        this.abortReason = reason.trim();
        this.abortedAt = abortedAt;
        this.alerts.forEach(Alert::deactivate);

        recordTimeline("TOUR_ABORTED", "Tour execution aborted: " + this.abortReason, abortedAt);
        registerDomainEvent(new TourAborted(this.id, this.abortReason, abortedAt));
    }

    public long getCheckedInCount() {
        return this.participants.stream().filter(ParticipantStatus::isPresent).count();
    }

    public Optional<Checkpoint> getNextPendingCheckpoint() {
        return this.checkpoints.stream()
                .filter(cp -> !cp.isReached())
                .min(Comparator.comparingInt(Checkpoint::getSequenceOrder));
    }

    public long getCurrentDelayMinutes() {
        return this.checkpoints.stream()
                .filter(Checkpoint::isReached)
                .max(Comparator.comparingInt(Checkpoint::getSequenceOrder))
                .map(Checkpoint::getDelayMinutes)
                .orElse(0L);
    }

    public long getTotalDelayMinutes() {
        return this.checkpoints.stream()
                .filter(Checkpoint::isReached)
                .mapToLong(Checkpoint::getDelayMinutes)
                .sum();
    }

    public long getTotalDurationMinutes() {
        if (this.startedAt == null) {
            return 0L;
        }
        Instant end = this.completedAt != null
                ? this.completedAt
                : (this.abortedAt != null ? this.abortedAt : Instant.now());
        return Math.max(0L, Duration.between(this.startedAt, end).toMinutes());
    }

    public boolean hasParticipantTourist(Long touristUserId) {
        if (touristUserId == null) {
            return false;
        }
        return this.participants.stream()
                .anyMatch(p -> touristUserId.equals(p.getTouristId()) || touristUserId.equals(p.getParticipantId()));
    }

    public List<Checkpoint> getCheckpoints() {
        return Collections.unmodifiableList(checkpoints);
    }

    public List<ParticipantStatus> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    public List<Incident> getIncidents() {
        return Collections.unmodifiableList(incidents);
    }

    public List<Alert> getAlerts() {
        return Collections.unmodifiableList(alerts);
    }

    public List<TimelineEntry> getTimeline() {
        return Collections.unmodifiableList(timeline);
    }

    private void addAlert(Alert alert) {
        this.alerts.add(alert);
    }

    private long nextAlertId() {
        return this.alerts.size() + 1L;
    }

    private void recordTimeline(String eventType, String description, Instant occurredAt) {
        long entryId = this.timeline.size() + 1L;
        this.timeline.add(new TimelineEntry(entryId, eventType, description, occurredAt));
    }
}
