package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands;

import org.jspecify.annotations.NullMarked;

/**
 * Internal command dispatched when a payment is completed to automatically activate
 * or update the agency's subscription to the paid commercial plan.
 *
 * @param agencyId  The agency identifier
 * @param planId    The paid commercial plan identifier
 * @param paymentId The settled payment identifier that triggered activation
 */
@NullMarked
public record ActivateSubscriptionOnPaymentCompletedCommand(
        Long agencyId,
        Long planId,
        Long paymentId
) {
    public ActivateSubscriptionOnPaymentCompletedCommand {
        if (agencyId == null || agencyId < 1) {
            throw new IllegalArgumentException("Agency id must be at least 1");
        }
        if (planId == null || planId < 1) {
            throw new IllegalArgumentException("Plan id must be at least 1");
        }
        if (paymentId == null || paymentId < 1) {
            throw new IllegalArgumentException("Payment id must be at least 1");
        }
    }
}
