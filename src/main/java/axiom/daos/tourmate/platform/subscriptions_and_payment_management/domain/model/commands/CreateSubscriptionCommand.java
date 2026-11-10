package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands;

import org.jspecify.annotations.NullMarked;

/**
 * Command to subscribe an agency to a commercial plan.
 *
 * @param agencyId The subscribing agency identifier
 * @param planId The selected plan identifier
 */
@NullMarked
public record CreateSubscriptionCommand(
        Long agencyId,
        Long planId
) {
    public CreateSubscriptionCommand {
        if (agencyId == null || agencyId < 1) {
            throw new IllegalArgumentException("Agency id must be at least 1");
        }
        if (planId == null || planId < 1) {
            throw new IllegalArgumentException("Plan id must be at least 1");
        }
    }
}
