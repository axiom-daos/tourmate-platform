package axiom.daos.tourmate.platform.tourmonitoring.domain.model.events;

import java.time.Instant;

public record TourStarted(
        Long tourExecutionId,
        Long tourId,
        Long guideId,
        Instant startedAt
) {
}
