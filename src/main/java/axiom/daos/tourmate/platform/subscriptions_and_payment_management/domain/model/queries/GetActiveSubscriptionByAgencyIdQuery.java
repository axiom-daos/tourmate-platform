package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries;

import org.jspecify.annotations.NullMarked;

/**
 * Query to retrieve the currently active subscription for a specific agency.
 *
 * @param agencyId The agency identifier
 */
@NullMarked
public record GetActiveSubscriptionByAgencyIdQuery(Long agencyId) {
    public GetActiveSubscriptionByAgencyIdQuery {
        if (agencyId == null || agencyId < 1) {
            throw new IllegalArgumentException("Agency id must be at least 1");
        }
    }
}
