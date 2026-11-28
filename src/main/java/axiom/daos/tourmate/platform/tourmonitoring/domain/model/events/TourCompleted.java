package axiom.daos.tourmate.platform.tourmonitoring.domain.model.events;

import java.time.Instant;

public record TourCompleted(
        Long tourExecutionId,
        Instant completedAt,
        long totalDurationMinutes,
        int reachedCheckpoints,
        int totalIncidents
) {
}
