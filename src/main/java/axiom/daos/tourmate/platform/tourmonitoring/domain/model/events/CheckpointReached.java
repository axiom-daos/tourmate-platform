package axiom.daos.tourmate.platform.tourmonitoring.domain.model.events;

import java.time.Instant;

public record CheckpointReached(
        Long tourExecutionId,
        Long checkpointId,
        String checkpointName,
        Instant reachedAt,
        long delayMinutes,
        boolean delayedBeyondTolerance
) {
}
