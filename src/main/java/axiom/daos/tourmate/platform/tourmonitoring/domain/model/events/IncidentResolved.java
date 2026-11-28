package axiom.daos.tourmate.platform.tourmonitoring.domain.model.events;

import java.time.Instant;

public record IncidentResolved(
        Long tourExecutionId,
        Long incidentId,
        String resolutionNotes,
        Instant resolvedAt
) {
}
