package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands;

import org.jspecify.annotations.NullMarked;

/**
 * Command to delete an existing plan from the catalog.
 *
 * @param planId Plan identifier to delete
 */
@NullMarked
public record DeletePlanCommand(Long planId) {
    public DeletePlanCommand {
        if (planId == null || planId < 1) {
            throw new IllegalArgumentException("Plan id must be at least 1");
        }
    }
}
