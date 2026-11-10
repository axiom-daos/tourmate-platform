package axiom.daos.tourmate.platform.safety_and_incident_management.domain.persistence;

import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.aggregates.Incident;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Optional;

@NullMarked
public interface IncidentRepository {
    List<Incident> findAll();
    Optional<Incident> findById(Long id);
    Optional<Incident> findByUuid(String uuid);
    Incident save(Incident incident);
    void deleteById(Long id);
    boolean existsById(Long id);
}