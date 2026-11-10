package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries;

import org.jspecify.annotations.NullMarked;

/**
 * Query to retrieve a specific plan by its identifier.
 *
 * @param planId The plan unique identifier
 */
@NullMarked
public record GetPlanByIdQuery(Long planId) {
    public GetPlanByIdQuery {
        if (planId == null || planId < 1) {
            throw new IllegalArgumentException("Plan id must be at least 1");
        }
    }
}
