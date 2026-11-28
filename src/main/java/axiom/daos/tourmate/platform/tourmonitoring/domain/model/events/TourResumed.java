package axiom.daos.tourmate.platform.tourmonitoring.domain.model.events;

import java.time.Instant;

public record TourResumed(
        Long tourExecutionId,
        Instant resumedAt
) {
}
