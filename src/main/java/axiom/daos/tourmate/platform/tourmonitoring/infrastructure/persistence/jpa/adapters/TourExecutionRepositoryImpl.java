package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.adapters;

import axiom.daos.tourmate.platform.shared.infrastructure.events.DomainEventPublisher;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.TourExecution;
import axiom.daos.tourmate.platform.tourmonitoring.domain.repositories.TourExecutionRepository;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.assemblers.TourExecutionPersistenceAssembler;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.repositories.TourExecutionPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adapter bridging {@link TourExecutionRepository} with Spring Data JPA and publishing domain events.
 */
@Repository
public class TourExecutionRepositoryImpl implements TourExecutionRepository {

    private final TourExecutionPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public TourExecutionRepositoryImpl(
            TourExecutionPersistenceRepository persistenceRepository,
            DomainEventPublisher domainEventPublisher
    ) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<TourExecution> findById(Long id) {
        return persistenceRepository.findById(id)
                .map(TourExecutionPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public TourExecution save(TourExecution execution) {
        var entity = TourExecutionPersistenceAssembler.toPersistenceFromDomain(execution);
        var saved = persistenceRepository.saveAndFlush(entity);
        var savedDomain = TourExecutionPersistenceAssembler.toDomainFromPersistence(saved);
        domainEventPublisher.publishAndClearEvents(execution);
        return savedDomain;
    }

    @Override
    public boolean existsById(Long id) {
        return persistenceRepository.existsById(id);
    }
}
