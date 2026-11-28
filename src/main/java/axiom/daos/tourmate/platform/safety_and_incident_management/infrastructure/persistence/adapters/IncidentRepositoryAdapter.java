package axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.adapters;

import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.aggregates.Incident;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.persistence.IncidentRepository;
import axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.assemblers.IncidentPersistenceAssembler;
import axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.entities.IncidentJpaEntity;
import axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.repositories.IncidentJpaRepository;
import axiom.daos.tourmate.platform.shared.infrastructure.events.DomainEventPublisher;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@NullMarked
@Repository
public class IncidentRepositoryAdapter implements IncidentRepository {

    private final IncidentJpaRepository jpaRepository;
    private final DomainEventPublisher domainEventPublisher;

    public IncidentRepositoryAdapter(
            IncidentJpaRepository jpaRepository,
            DomainEventPublisher domainEventPublisher
    ) {
        this.jpaRepository = jpaRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public List<Incident> findAll() {
        return jpaRepository.findAll().stream()
                .map(IncidentPersistenceAssembler::toEntityFromJpaEntity)
                .toList();
    }

    @Override
    public Optional<Incident> findById(Long id) {
        return jpaRepository.findById(id)
                .map(IncidentPersistenceAssembler::toEntityFromJpaEntity);
    }

    @Override
    public Incident save(Incident incident) {
        IncidentJpaEntity entity = IncidentPersistenceAssembler.toJpaEntityFromEntity(incident);
        IncidentJpaEntity savedEntity = jpaRepository.save(entity);
        Incident savedIncident = IncidentPersistenceAssembler.toEntityFromJpaEntity(savedEntity);
        domainEventPublisher.publishAndClearEvents(incident);
        return savedIncident;
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}