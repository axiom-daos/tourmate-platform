package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Date;

/**
 * REST response resource for a payment transaction.
 */
@Schema(name = "PaymentResponse", description = "Subscription payment ledger transaction")
public record PaymentResource(
        @Schema(description = "Payment unique identifier", example = "101")
        Long id,

        @Schema(description = "Agency identifier", example = "42")
        Long agencyId,

        @Schema(description = "Plan identifier", example = "2")
        Long planId,

        @Schema(description = "Monetary amount paid", example = "79.00")
        BigDecimal amount,

        @Schema(description = "ISO currency code", example = "USD")
        String currency,

        @Schema(description = "Settlement status", example = "PAID")
        String status,

        @Schema(description = "Voucher or transaction reference", example = "REC-00001")
        String transactionReference,

        @Schema(description = "Transaction request timestamp")
        Date requestedAt
) {
}
