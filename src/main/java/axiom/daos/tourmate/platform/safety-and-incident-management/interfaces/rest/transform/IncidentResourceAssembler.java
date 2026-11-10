package axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.aggregates.Incident;
import axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest.resources.IncidentResource;
import org.jspecify.annotations.NullMarked;

@NullMarked
/**
 * Provides utility methods to transform Incident domain aggregates into IncidentResource representations for RESTful API responses.
 */
public final class IncidentResourceAssembler {
    /**
     * Private constructor to prevent instantiation of the utility class.
     */
    private IncidentResourceAssembler() {
    }

    /**
     * Transforms an Incident domain aggregate into an IncidentResource representation.
     * @param entity
     * @return
     */
    public static IncidentResource toResourceFromEntity(Incident entity) {
        return new IncidentResource(
                entity.getId(),
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