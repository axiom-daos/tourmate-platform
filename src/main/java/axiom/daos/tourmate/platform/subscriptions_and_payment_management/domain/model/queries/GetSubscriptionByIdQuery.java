package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries;

import org.jspecify.annotations.NullMarked;

/**
 * Query to retrieve an agency subscription by its identifier.
 *
 * @param subscriptionId The subscription identifier
 */
@NullMarked
public record GetSubscriptionByIdQuery(Long subscriptionId) {
    public GetSubscriptionByIdQuery {
        if (subscriptionId == null || subscriptionId < 1) {
            throw new IllegalArgumentException("Subscription id must be at least 1");
        }
    }
}
