package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands;

import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;

/**
 * Command to record a subscription payment transaction.
 *
 * @param agencyId The agency identifier
 * @param planId The plan identifier
 * @param amount The transaction amount
 * @param currency The transaction currency
 * @param transactionReference Transaction or voucher reference code
 */
@NullMarked
public record CreatePaymentCommand(
        Long agencyId,
        Long planId,
        BigDecimal amount,
        String currency,
        String transactionReference
) {
    public CreatePaymentCommand {
        if (agencyId == null || agencyId < 1) {
            throw new IllegalArgumentException("Agency id must be at least 1");
        }
        if (planId == null || planId < 1) {
            throw new IllegalArgumentException("Plan id must be at least 1");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
    }
}
