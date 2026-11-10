package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.queryservices;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Subscription;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetAllSubscriptionsQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetSubscriptionByIdQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetSubscriptionsByAgencyIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Query application service interface for the {@link Subscription} aggregate.
 */
public interface SubscriptionQueryService {

    /**
     * Handles retrieval of a subscription by its identifier.
     *
     * @param query Query containing subscription id
     * @return Optional containing Subscription if found
     */
    Optional<Subscription> handle(GetSubscriptionByIdQuery query);

    /**
     * Handles retrieval of all subscriptions across the platform.
     *
     * @param query Marker query
     * @return List of all subscriptions
     */
    List<Subscription> handle(GetAllSubscriptionsQuery query);

    /**
     * Handles retrieval of subscriptions for a specific agency.
     *
     * @param query Query containing agency id
     * @return List of subscriptions belonging to agency
     */
    List<Subscription> handle(GetSubscriptionsByAgencyIdQuery query);
}
