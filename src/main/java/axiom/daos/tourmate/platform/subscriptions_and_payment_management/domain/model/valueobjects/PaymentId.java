package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.valueobjects;

import org.jspecify.annotations.NullMarked;

/**
 * Value object representing a payment unique identifier.
 *
 * @param paymentId The payment identifier. Cannot be null or less than 1.
 */
@NullMarked
public record PaymentId(Long paymentId) {
    public PaymentId {
        if (paymentId == null || paymentId < 1) {
            throw new IllegalArgumentException("Payment id cannot be null or less than 1");
        }
    }
}
