package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries;

import org.jspecify.annotations.NullMarked;

/**
 * Query to retrieve subscriptions belonging to a given agency.
 *
 * @param agencyId The agency identifier
 */
@NullMarked
public record GetSubscriptionsByAgencyIdQuery(Long agencyId) {
    public GetSubscriptionsByAgencyIdQuery {
        if (agencyId == null || agencyId < 1) {
            throw new IllegalArgumentException("Agency id must be at least 1");
        }
    }
}
