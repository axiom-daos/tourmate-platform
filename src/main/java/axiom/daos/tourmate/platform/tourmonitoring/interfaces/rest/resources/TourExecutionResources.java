package axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources;

import axiom.daos.tourmate.platform.tourmonitoring.application.queryservices.TourExecutionQueryService;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.TourExecution;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Alert;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Checkpoint;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Incident;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.ParticipantStatus;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionAlertType;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionIncidentSeverity;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionIncidentStatus;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ParticipantAttendanceStatus;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.RiskLevel;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.util.List;

/**
 * Request and response DTO resources for {@code TourExecution} REST endpoints.
 */
public final class TourExecutionResources {

    private TourExecutionResources() {
    }

    @Schema(
            name = "CreateTourExecutionRequest",
            description = "Payload to schedule a new tour execution with checkpoints and participants",
            example = """
                    {
                      "tourId": 1,
                      "guideId": 2,
                      "bookingId": 100,
                      "scheduledStartAt": "2026-12-01T08:00:00Z",
                      "initialLatitude": -13.1631,
                      "initialLongitude": -72.5450,
                      "checkpoints": [
                        {
                          "id": 1,
                          "name": "Sun Gate",
                          "sequenceOrder": 1,
                          "latitude": -13.1650,
                          "longitude": -72.5400,
                          "expectedArrival": "2026-12-01T09:00:00Z",
                          "latestAllowedArrival": "2026-12-01T09:15:00Z"
                        }
                      ],
                      "participants": [
                        { "participantId": 10, "touristId": 3, "bookingId": 100 }
                      ]
                    }
                    """
    )
    public record CreateTourExecutionResource(
            @NotNull(message = "Tour ID is required")
            @Positive(message = "Tour ID must be positive")
            Long tourId,

            @NotNull(message = "Guide ID is required")
            @Positive(message = "Guide ID must be positive")
            Long guideId,

            Long bookingId,

            @NotNull(message = "Scheduled start timestamp is required")
            Instant scheduledStartAt,

            @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
            @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
            Double initialLatitude,

            @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
            @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
            Double initialLongitude,

            @Valid
            List<CheckpointSeedResource> checkpoints,

            @Valid
            List<ParticipantSeedResource> participants
    ) {
    }

    @Schema(name = "CheckpointSeedRequest")
    public record CheckpointSeedResource(
            @NotNull @Positive Long id,
            @NotBlank String name,
            @Min(1) int sequenceOrder,
            @DecimalMin("-90.0") @DecimalMax("90.0") double latitude,
            @DecimalMin("-180.0") @DecimalMax("180.0") double longitude,
            @NotNull Instant expectedArrival,
            @NotNull Instant latestAllowedArrival
    ) {
    }

    @Schema(name = "ParticipantSeedRequest")
    public record ParticipantSeedResource(
            @NotNull @Positive Long participantId,
            @NotNull @Positive Long touristId,
            Long bookingId
    ) {
    }

    @Schema(
            name = "ReportExecutionIncidentRequest",
            description = "Payload to report an incident during a tour execution",
            example = """
                    {
                      "type": "MEDICAL",
                      "severity": "MEDIUM",
                      "description": "Participant experiencing mild altitude sickness",
                      "latitude": -13.1642,
                      "longitude": -72.5431
                    }
                    """
    )
    public record ReportIncidentResource(
            @NotBlank(message = "Incident type is required")
            String type,

            @NotNull(message = "Incident severity is required")
            ExecutionIncidentSeverity severity,

            @NotBlank(message = "Incident description is required")
            String description,

            @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
            @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
            Double latitude,

            @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
            @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
            Double longitude
    ) {
    }

    @Schema(
            name = "ResolveExecutionIncidentRequest",
            description = "Payload with mandatory resolution notes to resolve an incident",
            example = "{\"resolutionNotes\": \"Oxygen administered and vital signs stabilized\"}"
    )
    public record ResolveIncidentResource(
            @NotBlank(message = "Resolution notes are required")
            String resolutionNotes
    ) {
    }

    @Schema(
            name = "TriggerSosRequest",
            description = "Payload to trigger an emergency SOS alert",
            example = "{\"reason\": \"Emergency medical assistance required at trail section 2\", \"latitude\": -13.165, \"longitude\": -72.541}"
    )
    public record TriggerSosResource(
            String reason,

            @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
            @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
            Double latitude,

            @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
            @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
            Double longitude
    ) {
    }

    @Schema(
            name = "ReasonRequest",
            description = "Mandatory reason payload for pause or abort commands",
            example = "{\"reason\": \"Severe weather warning on mountain pass\"}"
    )
    public record ReasonResource(
            @NotBlank(message = "Reason is required")
            String reason
    ) {
    }

    @Schema(name = "PagedActiveTourExecutionsResponse")
    public record PagedActiveExecutionsResource(
            List<TourExecutionQueryService.ActiveTourExecutionItemView> content,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
    }

    @Schema(name = "TourExecutionStateResponse")
    public record TourExecutionStateResource(
            Long id,
            Long tourId,
            Long guideId,
            Long bookingId,
            TourStatus status,
            RiskLevel riskLevel,
            Instant scheduledStartAt,
            Instant startedAt,
            Instant pausedAt,
            Instant completedAt,
            Instant abortedAt,
            String pauseReason,
            String abortReason,
            long checkedInCount,
            int totalParticipants,
            int reachedCheckpoints,
            int totalCheckpoints
    ) {
        public static TourExecutionStateResource fromDomain(TourExecution execution) {
            int reached = (int) execution.getCheckpoints().stream().filter(Checkpoint::isReached).count();
            return new TourExecutionStateResource(
                    execution.getId(),
                    execution.getTourId(),
                    execution.getGuideId(),
                    execution.getBookingId(),
                    execution.getStatus(),
                    execution.getRiskLevel(),
                    execution.getScheduledStartAt(),
                    execution.getStartedAt(),
                    execution.getPausedAt(),
                    execution.getCompletedAt(),
                    execution.getAbortedAt(),
                    execution.getPauseReason(),
                    execution.getAbortReason(),
                    execution.getCheckedInCount(),
                    execution.getParticipants().size(),
                    reached,
                    execution.getCheckpoints().size()
            );
        }
    }

    @Schema(name = "ParticipantCheckInResponse")
    public record ParticipantCheckInResource(
            Long participantId,
            Long touristId,
            Long bookingId,
            ParticipantAttendanceStatus status,
            Instant checkedInAt
    ) {
        public static ParticipantCheckInResource fromDomain(ParticipantStatus p) {
            return new ParticipantCheckInResource(
                    p.getParticipantId(),
                    p.getTouristId(),
                    p.getBookingId(),
                    p.getStatus(),
                    p.getCheckedInAt()
            );
        }
    }

    @Schema(name = "CheckpointReachedResponse")
    public record CheckpointReachedResource(
            Long id,
            String name,
            int sequenceOrder,
            double latitude,
            double longitude,
            Instant expectedArrival,
            Instant latestAllowedArrival,
            boolean reached,
            Instant reachedAt,
            long delayMinutes,
            boolean delayedBeyondTolerance
    ) {
        public static CheckpointReachedResource fromDomain(Checkpoint cp) {
            return new CheckpointReachedResource(
                    cp.getId(),
                    cp.getName(),
                    cp.getSequenceOrder(),
                    cp.getLocation().latitude(),
                    cp.getLocation().longitude(),
                    cp.getTimeWindow().expectedArrival(),
                    cp.getTimeWindow().latestAllowedArrival(),
                    cp.isReached(),
                    cp.getReachedAt(),
                    cp.getDelayMinutes(),
                    cp.isDelayedBeyondTolerance()
            );
        }
    }

    @Schema(name = "ExecutionIncidentResponse")
    public record ExecutionIncidentResource(
            Long id,
            String type,
            ExecutionIncidentSeverity severity,
            ExecutionIncidentStatus status,
            String description,
            Double latitude,
            Double longitude,
            Long reportedById,
            String reportedByRole,
            String resolutionNotes,
            Instant reportedAt,
            Instant resolvedAt
    ) {
        public static ExecutionIncidentResource fromDomain(Incident inc) {
            return new ExecutionIncidentResource(
                    inc.getId(),
                    inc.getType(),
                    inc.getSeverity(),
                    inc.getStatus(),
                    inc.getDescription(),
                    inc.getLocation() != null ? inc.getLocation().latitude() : null,
                    inc.getLocation() != null ? inc.getLocation().longitude() : null,
                    inc.getReportedById(),
                    inc.getReportedByRole(),
                    inc.getResolutionNotes(),
                    inc.getReportedAt(),
                    inc.getResolvedAt()
            );
        }
    }

    @Schema(name = "ExecutionAlertResponse")
    public record ExecutionAlertResource(
            Long id,
            ExecutionAlertType type,
            RiskLevel riskLevel,
            String message,
            Long sourceUserId,
            String sourceRole,
            Double latitude,
            Double longitude,
            boolean active,
            Instant createdAt
    ) {
        public static ExecutionAlertResource fromDomain(Alert a) {
            return new ExecutionAlertResource(
                    a.getId(),
                    a.getType(),
                    a.getSeverity(),
                    a.getMessage(),
                    a.getSourceUserId(),
                    a.getSourceRole(),
                    a.getLocation() != null ? a.getLocation().latitude() : null,
                    a.getLocation() != null ? a.getLocation().longitude() : null,
                    a.isActive(),
                    a.getCreatedAt()
            );
        }
    }
}
