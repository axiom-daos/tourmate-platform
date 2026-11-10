package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries;

import org.jspecify.annotations.NullMarked;

/**
 * Query to retrieve a payment record by its identifier.
 *
 * @param paymentId The payment identifier
 */
@NullMarked
public record GetPaymentByIdQuery(Long paymentId) {
    public GetPaymentByIdQuery {
        if (paymentId == null || paymentId < 1) {
            throw new IllegalArgumentException("Payment id must be at least 1");
        }
    }
}
