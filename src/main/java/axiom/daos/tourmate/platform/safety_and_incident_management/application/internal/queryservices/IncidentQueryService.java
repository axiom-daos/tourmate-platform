package axiom.daos.tourmate.platform.safety_and_incident_management.application.internal.queryservices;

import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.aggregates.Incident;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.persistence.IncidentRepository;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service handling read queries for incidents.
 */
@NullMarked
@Service
@Transactional(readOnly = true)
public class IncidentQueryService {

    private final IncidentRepository incidentRepository;

    public IncidentQueryService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    public Optional<Incident> getIncidentById(@Nullable Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return incidentRepository.findById(id);
    }

    public Optional<Incident> getIncidentByUuid(@Nullable UUID uuid) {
        if (uuid == null) {
            return Optional.empty();
        }
        return incidentRepository.findAll().stream()
                .filter(incident -> uuid.equals(incident.getUuid()))
                .findFirst();
    }
}