package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Payment;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for the {@link Payment} aggregate.
 */
public interface PaymentRepository {

    /**
     * Find a payment by its unique identifier.
     *
     * @param id The payment id
     * @return Optional containing the payment if found
     */
    Optional<Payment> findById(Long id);

    /**
     * Retrieve all payment transactions.
     *
     * @return List of all payments
     */
    List<Payment> findAll();

    /**
     * Retrieve all payment transactions for a given agency.
     *
     * @param agencyId The agency identifier
     * @return List of payments associated with the agency
     */
    List<Payment> findByAgencyId(Long agencyId);

    /**
     * Retrieve all payment transactions associated with a given subscription.
     *
     * @param subscriptionId The subscription identifier
     * @return List of payments for the subscription
     */
    List<Payment> findBySubscriptionId(Long subscriptionId);

    /**
     * Check if a payment exists by its identifier.
     *
     * @param id The payment id
     * @return True if exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Persist a payment transaction.
     *
     * @param payment The payment aggregate
     * @return The persisted payment aggregate
     */
    Payment save(Payment payment);
}
