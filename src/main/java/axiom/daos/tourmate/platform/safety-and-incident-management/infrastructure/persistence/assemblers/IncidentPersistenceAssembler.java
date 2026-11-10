package axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.assemblers;

import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.aggregates.Incident;
import axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.entities.IncidentJpaEntity;
import org.jspecify.annotations.NullMarked;

/**
 * Assembler to map between Incident domain aggregates and IncidentJpaEntity persistence entities.
 */
@NullMarked
public final class IncidentPersistenceAssembler {

    private IncidentPersistenceAssembler() {
        // Private constructor to prevent instantiation
    }

    /**
     * Maps a JPA persistence entity to a Domain aggregate.
     *
     * @param entity the IncidentJpaEntity
     * @return the Incident domain aggregate
     */
    public static Incident toEntityFromJpaEntity(IncidentJpaEntity entity) {
        return Incident.reconstitute(
                entity.getId(),
                entity.getUuid(),
                entity.getActiveTourId(),
                entity.getReportedByUserId(),
                entity.getDescription(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getReportedAt(),
                entity.getStatus(),
                entity.getSeverity(),
                entity.getResolutionNote(),
                entity.getReporterRole()
        );
    }

    /**
     * Maps a Domain aggregate to a JPA persistence entity.
     *
     * @param incident the Incident domain aggregate
     * @return the IncidentJpaEntity persistence entity
     */
    public static IncidentJpaEntity toJpaEntityFromEntity(Incident incident) {
        IncidentJpaEntity entity = new IncidentJpaEntity();
        if (incident.getId() != null) {
            entity.setId(incident.getId());
        }
        entity.setUuid(incident.getUuid());
        entity.setActiveTourId(incident.getActiveTourId());
        entity.setReportedByUserId(incident.getReportedByUserId());
        entity.setDescription(incident.getDescription());
        entity.setLatitude(incident.getLatitude());
        entity.setLongitude(incident.getLongitude());
        entity.setReportedAt(incident.getReportedAt());
        entity.setStatus(incident.getStatus());
        entity.setSeverity(incident.getSeverity());
        entity.setResolutionNote(incident.getResolutionNote());
        entity.setReporterRole(incident.getReporterRole());
        return entity;
    }
}
