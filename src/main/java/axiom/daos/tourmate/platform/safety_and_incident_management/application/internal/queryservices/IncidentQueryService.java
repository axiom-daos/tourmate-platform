package axiom.daos.tourmate.platform.safety_and_incident_management.application.internal.queryservices;

import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.aggregates.Incident;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.persistence.IncidentRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@NullMarked
@Service
/**
 * Service class for handling queries related to incidents, such as retrieving all incidents or a specific incident by ID.
 */
public class IncidentQueryService {
    /**
     * The repository used for incident data access.
     */
    private final IncidentRepository incidentRepository;
/**
     * Constructs an IncidentQueryService with the specified IncidentRepository.
     *
     * @param incidentRepository the repository used for incident data access
     */
    public IncidentQueryService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }
/**
     * Retrieves all incidents from the repository.
     *
     * @return a list of all incidents
     */
    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }
/**
     * Retrieves an incident by its ID from the repository.
     *
     * @param id the ID of the incident to retrieve
     * @return an Optional containing the incident if found, or empty if not found
     */
    public Optional<Incident> getIncidentById(Long id) {
        return incidentRepository.findById(id);
    }

}