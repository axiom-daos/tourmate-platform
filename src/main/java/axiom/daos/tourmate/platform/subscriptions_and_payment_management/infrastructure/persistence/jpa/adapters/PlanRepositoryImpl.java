package axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.adapters;

import axiom.daos.tourmate.platform.shared.infrastructure.events.DomainEventPublisher;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Plan;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories.PlanRepository;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.assemblers.PlanPersistenceAssembler;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.repositories.PlanPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter bridging {@link PlanRepository} with Spring Data JPA.
 */
@Repository
public class PlanRepositoryImpl implements PlanRepository {

    private final PlanPersistenceRepository planPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public PlanRepositoryImpl(
            PlanPersistenceRepository planPersistenceRepository,
            DomainEventPublisher domainEventPublisher
    ) {
        this.planPersistenceRepository = planPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<Plan> findById(Long id) {
        return planPersistenceRepository.findById(id).map(PlanPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Plan> findAll() {
        return planPersistenceRepository.findAll().stream()
                .map(PlanPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<Plan> findByName(String name) {
        return planPersistenceRepository.findByName(name).map(PlanPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsById(Long id) {
        return planPersistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return planPersistenceRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdIsNot(String name, Long id) {
        return planPersistenceRepository.existsByNameAndIdIsNot(name, id);
    }

    @Override
    public Plan save(Plan plan) {
        var entity = PlanPersistenceAssembler.toPersistenceFromDomain(plan);
        var savedEntity = planPersistenceRepository.save(entity);
        var savedPlan = PlanPersistenceAssembler.toDomainFromPersistence(savedEntity);
        domainEventPublisher.publishAndClearEvents(plan);
        return savedPlan;
    }

    @Override
    public void delete(Plan plan) {
        if (plan != null && plan.getId() != null) {
            planPersistenceRepository.deleteById(plan.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        planPersistenceRepository.deleteById(id);
    }
}
