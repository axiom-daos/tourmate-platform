package axiom.daos.tourmate.platform.safety_and_incident_management.domain.persistence;

import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.aggregates.Incident;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Optional;

@NullMarked
/**
 * Repository interface for managing Incident entities, providing methods for CRUD operations and existence checks.
 */
public interface IncidentRepository {
    List<Incident> findAll();
    Optional<Incident> findById(Long id);
    Incident save(Incident incident);
    void deleteById(Long id);
    boolean existsById(Long id);
}