package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.valueobjects;

import org.jspecify.annotations.NullMarked;

/**
 * Value object representing a subscription unique identifier.
 *
 * @param subscriptionId The subscription identifier. Cannot be null or less than 1.
 */
@NullMarked
public record SubscriptionId(Long subscriptionId) {
    public SubscriptionId {
        if (subscriptionId == null || subscriptionId < 1) {
            throw new IllegalArgumentException("Subscription id cannot be null or less than 1");
        }
    }
}
