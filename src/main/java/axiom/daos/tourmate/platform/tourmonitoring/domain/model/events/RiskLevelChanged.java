package axiom.daos.tourmate.platform.tourmonitoring.domain.model.events;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.RiskLevel;

import java.time.Instant;

public record RiskLevelChanged(
        Long tourExecutionId,
        RiskLevel previousRiskLevel,
        RiskLevel newRiskLevel,
        String reason,
        Instant occurredAt
) {
}
