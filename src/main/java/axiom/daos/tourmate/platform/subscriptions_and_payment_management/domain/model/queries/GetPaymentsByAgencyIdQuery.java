package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries;

import org.jspecify.annotations.NullMarked;

/**
 * Query to retrieve payments recorded for a given agency.
 *
 * @param agencyId The agency identifier
 */
@NullMarked
public record GetPaymentsByAgencyIdQuery(Long agencyId) {
    public GetPaymentsByAgencyIdQuery {
        if (agencyId == null || agencyId < 1) {
            throw new IllegalArgumentException("Agency id must be at least 1");
        }
    }
}
