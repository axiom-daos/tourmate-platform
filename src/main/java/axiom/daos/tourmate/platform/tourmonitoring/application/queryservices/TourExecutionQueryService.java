package axiom.daos.tourmate.platform.tourmonitoring.application.queryservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionAlertType;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionIncidentSeverity;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionIncidentStatus;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ParticipantAttendanceStatus;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.RiskLevel;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Read-only query service contract for {@code TourExecution} projections.
 */
public interface TourExecutionQueryService {

    record CheckpointView(
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
    }

    record AlertView(
            Long id,
            ExecutionAlertType type,
            RiskLevel severity,
            String message,
            Long sourceUserId,
            String sourceRole,
            Double latitude,
            Double longitude,
            boolean active,
            Instant createdAt
    ) {
    }

    record IncidentView(
            Long id,
            String type,
            ExecutionIncidentSeverity severity,
            String description,
            Double latitude,
            Double longitude,
            Long reportedById,
            String reportedByRole,
            ExecutionIncidentStatus status,
            String resolutionNotes,
            Instant reportedAt,
            Instant resolvedAt
    ) {
    }

    record ParticipantItemView(
            Long participantId,
            Long touristId,
            Long bookingId,
            ParticipantAttendanceStatus status,
            Instant checkedInAt
    ) {
    }

    record ParticipantsStatusView(
            Long tourExecutionId,
            int totalParticipants,
            int presentCount,
            int absentCount,
            int pendingCount,
            List<ParticipantItemView> participants
    ) {
    }

    record TimelineItemView(
            Long id,
            String eventType,
            String description,
            Instant occurredAt
    ) {
    }

    record ActiveTourExecutionItemView(
            Long id,
            Long tourId,
            Long guideId,
            Long bookingId,
            TourStatus status,
            RiskLevel riskLevel,
            Instant scheduledStartAt,
            Instant startedAt,
            int reachedCheckpoints,
            int totalCheckpoints,
            int openIncidentsCount,
            int activeAlertsCount,
            long currentDelayMinutes
    ) {
    }

    record TourExecutionLiveView(
            Long id,
            Long tourId,
            Long guideId,
            Long bookingId,
            TourStatus status,
            RiskLevel riskLevel,
            Double currentLatitude,
            Double currentLongitude,
            Instant scheduledStartAt,
            Instant startedAt,
            Instant pausedAt,
            String pauseReason,
            long currentDelayMinutes,
            CheckpointView nextCheckpoint,
            List<CheckpointView> checkpoints,
            List<ParticipantItemView> participants,
            List<IncidentView> incidents,
            List<AlertView> activeAlerts
    ) {
    }

    record TourExecutionSummaryView(
            Long id,
            Long tourId,
            Long guideId,
            TourStatus status,
            RiskLevel finalRiskLevel,
            Instant scheduledStartAt,
            Instant startedAt,
            Instant completedAt,
            Instant abortedAt,
            String abortReason,
            long totalDurationMinutes,
            int reachedCheckpointsCount,
            int totalCheckpointsCount,
            long totalDelayMinutes,
            int delayedCheckpointsCount,
            int totalIncidentsCount,
            int resolvedIncidentsCount,
            int unresolvedIncidentsCount,
            int presentParticipantsCount,
            int totalParticipantsCount,
            List<CheckpointView> checkpoints,
            List<IncidentView> incidents
    ) {
    }

    Result<Page<ActiveTourExecutionItemView>, ApplicationError> findActiveExecutions(
            TourStatus status,
            RiskLevel riskLevel,
            Long guideId,
            Instant fromDate,
            Instant toDate,
            Pageable pageable,
            Set<String> actorRoles
    );

    Result<TourExecutionLiveView, ApplicationError> getLiveState(
            Long tourExecutionId,
            Long actorUserId,
            Set<String> actorRoles
    );

    Result<List<TimelineItemView>, ApplicationError> getTimeline(
            Long tourExecutionId,
            Long actorUserId,
            Set<String> actorRoles
    );

    Result<ParticipantsStatusView, ApplicationError> getParticipantsStatus(
            Long tourExecutionId,
            Long actorUserId,
            Set<String> actorRoles
    );

    Result<TourExecutionLiveView, ApplicationError> getCurrentTourForGuide(
            Long guideUserId,
            Set<String> actorRoles
    );

    Result<TourExecutionLiveView, ApplicationError> getTourProgressForTourist(
            Long touristUserId,
            Set<String> actorRoles
    );

    Result<TourExecutionSummaryView, ApplicationError> getSummary(
            Long tourExecutionId,
            Long actorUserId,
            Set<String> actorRoles
    );
}
