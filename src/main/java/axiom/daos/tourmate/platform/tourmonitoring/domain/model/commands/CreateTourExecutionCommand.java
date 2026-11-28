package axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionIncidentSeverity;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Command to provision a new scheduled {@code TourExecution}.
 */
public record CreateTourExecutionCommand(
        Long tourId,
        Long guideId,
        Long bookingId,
        Instant scheduledStartAt,
        Double initialLatitude,
        Double initialLongitude,
        List<CheckpointSeed> checkpoints,
        List<ParticipantSeed> participants,
        Long actorUserId,
        Set<String> actorRoles
) {
    public record CheckpointSeed(
            Long id,
            String name,
            int sequenceOrder,
            double latitude,
            double longitude,
            Instant expectedArrival,
            Instant latestAllowedArrival
    ) {
    }

    public record ParticipantSeed(
            Long participantId,
            Long touristId,
            Long bookingId
    ) {
    }

    public record CheckInParticipantCommand(
            Long tourExecutionId,
            Long participantId,
            Long actorUserId,
            Set<String> actorRoles
    ) {
    }

    public record StartTourExecutionCommand(
            Long tourExecutionId,
            Long actorUserId,
            Set<String> actorRoles
    ) {
    }

    public record ReachCheckpointCommand(
            Long tourExecutionId,
            Long checkpointId,
            Long actorUserId,
            Set<String> actorRoles
    ) {
    }

    public record ReportIncidentCommand(
            Long tourExecutionId,
            String type,
            ExecutionIncidentSeverity severity,
            String description,
            Double latitude,
            Double longitude,
            Long actorUserId,
            Set<String> actorRoles
    ) {
    }

    public record ResolveIncidentCommand(
            Long tourExecutionId,
            Long incidentId,
            String resolutionNotes,
            Long actorUserId,
            Set<String> actorRoles
    ) {
    }

    public record TriggerSosCommand(
            Long tourExecutionId,
            String reason,
            Double latitude,
            Double longitude,
            Long actorUserId,
            Set<String> actorRoles
    ) {
    }

    public record PauseTourExecutionCommand(
            Long tourExecutionId,
            String reason,
            Long actorUserId,
            Set<String> actorRoles
    ) {
    }

    public record ResumeTourExecutionCommand(
            Long tourExecutionId,
            Long actorUserId,
            Set<String> actorRoles
    ) {
    }

    public record CompleteTourExecutionCommand(
            Long tourExecutionId,
            Long actorUserId,
            Set<String> actorRoles
    ) {
    }

    public record AbortTourExecutionCommand(
            Long tourExecutionId,
            String reason,
            Long actorUserId,
            Set<String> actorRoles
    ) {
    }
}
