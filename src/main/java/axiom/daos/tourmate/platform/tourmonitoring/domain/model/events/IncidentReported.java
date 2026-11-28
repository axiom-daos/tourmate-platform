package axiom.daos.tourmate.platform.tourmonitoring.domain.model.events;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionIncidentSeverity;

import java.time.Instant;

public record IncidentReported(
        Long tourExecutionId,
        Long incidentId,
        String type,
        ExecutionIncidentSeverity severity,
        Long reportedById,
        Instant reportedAt
) {
}
