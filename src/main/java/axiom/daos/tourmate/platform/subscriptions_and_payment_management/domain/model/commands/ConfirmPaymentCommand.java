package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands;

import org.jspecify.annotations.NullMarked;

/**
 * Command to confirm and settle a pending payment transaction.
 *
 * @param paymentId The identifier of the payment to confirm
 */
@NullMarked
public record ConfirmPaymentCommand(Long paymentId) {
    public ConfirmPaymentCommand {
        if (paymentId == null || paymentId < 1) {
            throw new IllegalArgumentException("Payment id must be at least 1");
        }
    }
}
