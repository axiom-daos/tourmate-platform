package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.entities;

import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionAlertType;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionIncidentSeverity;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionIncidentStatus;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ParticipantAttendanceStatus;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.RiskLevel;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA persistence entity for {@code TourExecution} with optimistic locking ({@code @Version})
 * and indexed query columns.
 */
@Entity
@Table(
        name = "tour_executions",
        indexes = {
                @Index(name = "idx_tour_executions_status", columnList = "status"),
                @Index(name = "idx_tour_executions_guide_id", columnList = "guide_id"),
                @Index(name = "idx_tour_executions_tour_id", columnList = "tour_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class TourExecutionPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @Column(name = "tour_id", nullable = false)
    private Long tourId;

    @Column(name = "guide_id", nullable = false)
    private Long guideId;

    @Column(name = "booking_id")
    private Long bookingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TourStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 30)
    private RiskLevel riskLevel;

    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    @Column(name = "scheduled_start_at", nullable = false)
    private Instant scheduledStartAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "paused_at")
    private Instant pausedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "aborted_at")
    private Instant abortedAt;

    @Column(name = "pause_reason", length = 500)
    private String pauseReason;

    @Column(name = "abort_reason", length = 500)
    private String abortReason;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tour_execution_checkpoints",
            joinColumns = @JoinColumn(name = "tour_execution_id")
    )
    @OrderColumn(name = "checkpoint_index")
    @BatchSize(size = 50)
    private List<CheckpointEmbeddable> checkpoints = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tour_execution_participants",
            joinColumns = @JoinColumn(name = "tour_execution_id")
    )
    @OrderColumn(name = "participant_index")
    @BatchSize(size = 50)
    private List<ParticipantStatusEmbeddable> participants = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tour_execution_incidents",
            joinColumns = @JoinColumn(name = "tour_execution_id")
    )
    @OrderColumn(name = "incident_index")
    @BatchSize(size = 50)
    private List<IncidentEmbeddable> incidents = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tour_execution_alerts",
            joinColumns = @JoinColumn(name = "tour_execution_id")
    )
    @OrderColumn(name = "alert_index")
    @BatchSize(size = 50)
    private List<AlertEmbeddable> alerts = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tour_execution_timeline",
            joinColumns = @JoinColumn(name = "tour_execution_id"),
            indexes = {
                    @Index(name = "idx_tour_execution_timeline_occurred_at", columnList = "occurred_at")
            }
    )
    @OrderColumn(name = "timeline_index")
    @BatchSize(size = 50)
    private List<TimelineEntryEmbeddable> timeline = new ArrayList<>();

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckpointEmbeddable {
        @Column(name = "checkpoint_id", nullable = false)
        private Long checkpointId;

        @Column(name = "name", nullable = false, length = 150)
        private String name;

        @Column(name = "sequence_order", nullable = false)
        private int sequenceOrder;

        @Column(name = "latitude", nullable = false)
        private double latitude;

        @Column(name = "longitude", nullable = false)
        private double longitude;

        @Column(name = "expected_arrival", nullable = false)
        private Instant expectedArrival;

        @Column(name = "latest_allowed_arrival", nullable = false)
        private Instant latestAllowedArrival;

        @Column(name = "reached", nullable = false)
        private boolean reached;

        @Column(name = "reached_at")
        private Instant reachedAt;

        @Column(name = "delay_minutes", nullable = false)
        private long delayMinutes;

        @Column(name = "delayed_beyond_tolerance", nullable = false)
        private boolean delayedBeyondTolerance;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipantStatusEmbeddable {
        @Column(name = "participant_id", nullable = false)
        private Long participantId;

        @Column(name = "tourist_id", nullable = false)
        private Long touristId;

        @Column(name = "booking_id")
        private Long bookingId;

        @Enumerated(EnumType.STRING)
        @Column(name = "attendance_status", nullable = false, length = 30)
        private ParticipantAttendanceStatus status;

        @Column(name = "checked_in_at")
        private Instant checkedInAt;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IncidentEmbeddable {
        @Column(name = "incident_id", nullable = false)
        private Long incidentId;

        @Column(name = "type", nullable = false, length = 100)
        private String type;

        @Enumerated(EnumType.STRING)
        @Column(name = "severity", nullable = false, length = 30)
        private ExecutionIncidentSeverity severity;

        @Column(name = "description", nullable = false, length = 1000)
        private String description;

        @Column(name = "latitude")
        private Double latitude;

        @Column(name = "longitude")
        private Double longitude;

        @Column(name = "reported_by_id", nullable = false)
        private Long reportedById;

        @Column(name = "reported_by_role", nullable = false, length = 30)
        private String reportedByRole;

        @Enumerated(EnumType.STRING)
        @Column(name = "status", nullable = false, length = 30)
        private ExecutionIncidentStatus status;

        @Column(name = "resolution_notes", length = 1000)
        private String resolutionNotes;

        @Column(name = "reported_at", nullable = false)
        private Instant reportedAt;

        @Column(name = "resolved_at")
        private Instant resolvedAt;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertEmbeddable {
        @Column(name = "alert_id", nullable = false)
        private Long alertId;

        @Enumerated(EnumType.STRING)
        @Column(name = "type", nullable = false, length = 30)
        private ExecutionAlertType type;

        @Enumerated(EnumType.STRING)
        @Column(name = "severity", nullable = false, length = 30)
        private RiskLevel severity;

        @Column(name = "message", nullable = false, length = 500)
        private String message;

        @Column(name = "source_user_id")
        private Long sourceUserId;

        @Column(name = "source_role", length = 30)
        private String sourceRole;

        @Column(name = "latitude")
        private Double latitude;

        @Column(name = "longitude")
        private Double longitude;

        @Column(name = "active", nullable = false)
        private boolean active;

        @Column(name = "created_at", nullable = false)
        private Instant createdAt;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimelineEntryEmbeddable {
        @Column(name = "entry_id", nullable = false)
        private Long entryId;

        @Column(name = "event_type", nullable = false, length = 60)
        private String eventType;

        @Column(name = "description", nullable = false, length = 1000)
        private String description;

        @Column(name = "occurred_at", nullable = false)
        private Instant occurredAt;
    }
}
