package axiom.daos.tourmate.platform.safety_and_incident_management.application.internal.queryservices;

import axiom.daos.tourmate.platform.safety.domain.model.aggregates.Incident;
import axiom.daos.tourmate.platform.safety.domain.model.repositories.IncidentRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@NullMarked
@Service
public class IncidentQueryService {

    private final IncidentRepository incidentRepository;

    public IncidentQueryService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    public Optional<Incident> getIncidentById(Long id) {
        return incidentRepository.findById(id);
    }

    public Optional<Incident> getIncidentByUuid(String uuid) {
        return incidentRepository.findByUuid(uuid);
    }
}