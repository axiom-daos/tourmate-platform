package axiom.daos.tourmate.platform.tourmonitoring.domain.model.events;

import java.time.Instant;

public record TourPaused(
        Long tourExecutionId,
        String reason,
        Instant pausedAt
) {
}
