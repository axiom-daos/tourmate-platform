package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands;

import org.jspecify.annotations.NullMarked;

/**
 * Command to cancel an active agency subscription.
 *
 * @param subscriptionId The subscription identifier to cancel
 * @param reason Optional textual cancellation feedback reason
 */
@NullMarked
public record CancelSubscriptionCommand(
        Long subscriptionId,
        String reason
) {
    public CancelSubscriptionCommand {
        if (subscriptionId == null || subscriptionId < 1) {
            throw new IllegalArgumentException("Subscription id must be at least 1");
        }
        if (reason == null) {
            reason = "";
        }
    }
}
