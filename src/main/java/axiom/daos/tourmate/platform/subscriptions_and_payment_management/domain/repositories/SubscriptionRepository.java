package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Subscription;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.valueobjects.SubscriptionStatus;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for the {@link Subscription} aggregate.
 */
public interface SubscriptionRepository {

    /**
     * Find a subscription by its unique identifier.
     *
     * @param id The subscription id
     * @return Optional containing the subscription if found
     */
    Optional<Subscription> findById(Long id);

    /**
     * Retrieve all subscriptions recorded across agencies.
     *
     * @return List of all subscriptions
     */
    List<Subscription> findAll();

    /**
     * Retrieve all subscriptions associated with a given agency.
     *
     * @param agencyId The agency identifier
     * @return List of subscriptions belonging to the agency
     */
    List<Subscription> findByAgencyId(Long agencyId);

    /**
     * Find the first subscription of an agency with a specific status (e.g. ACTIVE).
     *
     * @param agencyId The agency identifier
     * @param status   The subscription status
     * @return Optional subscription if one matches
     */
    Optional<Subscription> findFirstByAgencyIdAndStatus(Long agencyId, SubscriptionStatus status);

    /**
     * Check if a subscription exists by its identifier.
     *
     * @param id The subscription id
     * @return True if exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Save a subscription (create or update lifecycle).
     *
     * @param subscription The subscription aggregate
     * @return The persisted subscription aggregate
     */
    Subscription save(Subscription subscription);
}
