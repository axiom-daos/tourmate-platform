package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.valueobjects;

import org.jspecify.annotations.NullMarked;

/**
 * Value object representing a plan unique identifier.
 *
 * @param planId The plan identifier. Cannot be null or less than 1.
 */
@NullMarked
public record PlanId(Long planId) {
    public PlanId {
        if (planId == null || planId < 1) {
            throw new IllegalArgumentException("Plan id cannot be null or less than 1");
        }
    }
}
