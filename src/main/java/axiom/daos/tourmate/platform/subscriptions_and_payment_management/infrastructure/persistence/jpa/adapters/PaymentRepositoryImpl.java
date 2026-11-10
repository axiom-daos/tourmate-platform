package axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.adapters;

import axiom.daos.tourmate.platform.shared.infrastructure.events.DomainEventPublisher;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Payment;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories.PaymentRepository;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.assemblers.PaymentPersistenceAssembler;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.repositories.PaymentPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter bridging {@link PaymentRepository} with Spring Data JPA.
 */
@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentPersistenceRepository paymentPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public PaymentRepositoryImpl(
            PaymentPersistenceRepository paymentPersistenceRepository,
            DomainEventPublisher domainEventPublisher
    ) {
        this.paymentPersistenceRepository = paymentPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return paymentPersistenceRepository.findById(id)
                .map(PaymentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Payment> findAll() {
        return paymentPersistenceRepository.findAll().stream()
                .map(PaymentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Payment> findByAgencyId(Long agencyId) {
        return paymentPersistenceRepository.findByAgencyId(agencyId).stream()
                .map(PaymentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Payment> findBySubscriptionId(Long subscriptionId) {
        return List.of();
    }

    @Override
    public boolean existsById(Long id) {
        return paymentPersistenceRepository.existsById(id);
    }

    @Override
    public Payment save(Payment payment) {
        var entity = PaymentPersistenceAssembler.toPersistenceFromDomain(payment);
        var savedEntity = paymentPersistenceRepository.save(entity);
        var savedPayment = PaymentPersistenceAssembler.toDomainFromPersistence(savedEntity);
        domainEventPublisher.publishAndClearEvents(payment);
        return savedPayment;
    }
}
