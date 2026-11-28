package axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.assemblers;

import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.aggregates.Incident;
import axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.entities.IncidentJpaEntity;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public final class IncidentPersistenceAssembler {

    private IncidentPersistenceAssembler() {
    }

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

    public static IncidentJpaEntity toJpaEntityFromEntity(Incident incident) {
        IncidentJpaEntity entity = new IncidentJpaEntity();
        if (incident.getId() != null) {
            entity.setId(incident.getId());
        }
        entity.setUuid(incident.getUuid() != null ? incident.getUuid() : UUID.randomUUID());
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