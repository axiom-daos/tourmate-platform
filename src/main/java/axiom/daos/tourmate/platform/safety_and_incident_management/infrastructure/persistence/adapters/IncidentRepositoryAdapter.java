package axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.adapters;

import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.aggregates.Incident;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.persistence.IncidentRepository;
import axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.assemblers.IncidentPersistenceAssembler;
import axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.entities.IncidentJpaEntity;
import axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.repositories.IncidentJpaRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NullMarked
@Repository
/**
 * Adapter class that implements the IncidentRepository interface, providing methods to interact with the underlying JPA repository for Incident entities. It uses the IncidentPersistenceAssembler to convert between domain aggregates and JPA entities.
 */
public class IncidentRepositoryAdapter implements IncidentRepository {

    private final IncidentJpaRepository jpaRepository;

    /**
     * Constructs an IncidentRepositoryAdapter with the specified IncidentJpaRepository.
     * @param jpaRepository
     */
    public IncidentRepositoryAdapter(IncidentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    /**
     * Retrieves all incidents from the JPA repository, converting them to domain aggregates using the IncidentPersistenceAssembler.
     */
    public List<Incident> findAll() {
        return jpaRepository.findAll().stream()
                .map(IncidentPersistenceAssembler::toEntityFromJpaEntity)
                .collect(Collectors.toList());
    }

    @Override
    /**
     * Finds an incident by its ID in the JPA repository, converting it to a domain aggregate using the IncidentPersistenceAssembler if found.
     */
    public Optional<Incident> findById(Long id) {
        return jpaRepository.findById(id).map(IncidentPersistenceAssembler::toEntityFromJpaEntity);
    }

    @Override
    /**
     * Saves an incident to the JPA repository, converting it from a domain aggregate to a JPA entity using the IncidentPersistenceAssembler, and then converting the saved entity back to a domain aggregate.
     */
    public Incident save(Incident incident) {
        IncidentJpaEntity entity = IncidentPersistenceAssembler.toJpaEntityFromEntity(incident);
        IncidentJpaEntity savedEntity = jpaRepository.save(entity);
        return IncidentPersistenceAssembler.toEntityFromJpaEntity(savedEntity);
    }

    @Override
    /**
     * Deletes an incident by its ID from the JPA repository.
     */
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    /**
     * Checks if an incident with the specified ID exists in the JPA repository.
     */
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
