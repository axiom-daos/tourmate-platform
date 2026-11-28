package axiom.daos.tourmate.platform.tourmonitoring.domain.model.events;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.GeoLocation;

import java.time.Instant;

public record SosTriggered(
        Long tourExecutionId,
        Long alertId,
        Long triggeredByUserId,
        String triggeredByRole,
        String reason,
        GeoLocation location,
        Instant occurredAt
) {
}
