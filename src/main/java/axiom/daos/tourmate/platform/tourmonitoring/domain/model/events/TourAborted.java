package axiom.daos.tourmate.platform.tourmonitoring.domain.model.events;

import java.time.Instant;

public record TourAborted(
        Long tourExecutionId,
        String reason,
        Instant abortedAt
) {
}
