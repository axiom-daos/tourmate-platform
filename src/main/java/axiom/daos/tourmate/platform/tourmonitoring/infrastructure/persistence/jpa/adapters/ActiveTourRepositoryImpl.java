package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.adapters;

import axiom.daos.tourmate.platform.shared.infrastructure.events.DomainEventPublisher;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.ActiveTour;
import axiom.daos.tourmate.platform.tourmonitoring.domain.repositories.ActiveTourRepository;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.assemblers.ActiveTourPersistenceAssembler;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.repositories.ActiveTourPersistenceRepository;

import java.util.List;
import java.util.Optional;

public class ActiveTourRepositoryImpl  implements ActiveTourRepository {

    private final ActiveTourPersistenceRepository activeTourPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public ActiveTourRepositoryImpl(
            ActiveTourPersistenceRepository activeTourPersistenceRepository,
            DomainEventPublisher domainEventPublisher) {
        this.activeTourPersistenceRepository = activeTourPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }
    
    @Override
    public void deleteById(Long id) {
        activeTourPersistenceRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return activeTourPersistenceRepository.existsById(id);
    }

    @Override
    public List<ActiveTour> findAll() {
        return activeTourPersistenceRepository.findAll().stream().map(ActiveTourPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Optional<ActiveTour> findById(Long id) {
        return activeTourPersistenceRepository.findById(id).map(ActiveTourPersistenceAssembler::toDomainFromPersistence);
    }
}
