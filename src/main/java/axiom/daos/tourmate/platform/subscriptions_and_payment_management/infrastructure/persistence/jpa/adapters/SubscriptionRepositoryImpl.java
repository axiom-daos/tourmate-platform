package axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.adapters;

import axiom.daos.tourmate.platform.shared.infrastructure.events.DomainEventPublisher;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Subscription;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.valueobjects.SubscriptionStatus;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories.SubscriptionRepository;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.assemblers.SubscriptionPersistenceAssembler;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.repositories.SubscriptionPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter bridging {@link SubscriptionRepository} with Spring Data JPA.
 */
@Repository
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

    private final SubscriptionPersistenceRepository subscriptionPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public SubscriptionRepositoryImpl(
            SubscriptionPersistenceRepository subscriptionPersistenceRepository,
            DomainEventPublisher domainEventPublisher
    ) {
        this.subscriptionPersistenceRepository = subscriptionPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<Subscription> findById(Long id) {
        return subscriptionPersistenceRepository.findById(id)
                .map(SubscriptionPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Subscription> findAll() {
        return subscriptionPersistenceRepository.findAll().stream()
                .map(SubscriptionPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Subscription> findByAgencyId(Long agencyId) {
        return subscriptionPersistenceRepository.findByAgencyId(agencyId).stream()
                .map(SubscriptionPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<Subscription> findFirstByAgencyIdAndStatus(Long agencyId, SubscriptionStatus status) {
        return subscriptionPersistenceRepository.findFirstByAgencyIdAndStatus(agencyId, status)
                .map(SubscriptionPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsById(Long id) {
        return subscriptionPersistenceRepository.existsById(id);
    }

    @Override
    public Subscription save(Subscription subscription) {
        var entity = SubscriptionPersistenceAssembler.toPersistenceFromDomain(subscription);
        var savedEntity = subscriptionPersistenceRepository.save(entity);
        var savedSubscription = SubscriptionPersistenceAssembler.toDomainFromPersistence(savedEntity);
        domainEventPublisher.publishAndClearEvents(subscription);
        return savedSubscription;
    }
}
