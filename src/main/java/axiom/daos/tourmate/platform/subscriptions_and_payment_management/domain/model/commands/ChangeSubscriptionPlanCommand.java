package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands;

import org.jspecify.annotations.NullMarked;

/**
 * Command to change (upgrade or downgrade) the commercial plan of an active subscription.
 *
 * @param subscriptionId The subscription identifier
 * @param newPlanId      The target commercial plan identifier
 */
@NullMarked
public record ChangeSubscriptionPlanCommand(
        Long subscriptionId,
        Long newPlanId
) {
    public ChangeSubscriptionPlanCommand {
        if (subscriptionId == null || subscriptionId < 1) {
            throw new IllegalArgumentException("Subscription id must be at least 1");
        }
        if (newPlanId == null || newPlanId < 1) {
            throw new IllegalArgumentException("New plan id must be at least 1");
        }
    }
}
