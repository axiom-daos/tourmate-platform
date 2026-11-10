package axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.aggregates.Incident;
import axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest.resources.IncidentResource;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class IncidentResourceAssembler {

    private IncidentResourceAssembler() {
    }

    public static IncidentResource toResourceFromEntity(Incident entity) {
        return new IncidentResource(
                entity.getId(),
                entity.getUuid(),
                entity.getActiveTourId(),
                entity.getReportedByUserId(),
                entity.getDescription(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getReportedAt(),
                entity.getStatus().name(),
                entity.getSeverity().name(),
                entity.getResolutionNote(),
                entity.getReporterRole()
        );
    }
}