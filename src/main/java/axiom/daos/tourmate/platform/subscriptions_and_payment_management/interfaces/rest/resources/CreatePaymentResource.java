package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * REST request resource to record a payment transaction.
 */
@Schema(name = "CreatePaymentRequest", description = "Payload to record a subscription payment")
public record CreatePaymentResource(
        @Schema(description = "Agency identifier", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
        Long agencyId,

        @Schema(description = "Plan identifier", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        Long planId,

        @Schema(description = "Monetary amount", example = "79.00", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal amount,

        @Schema(description = "ISO currency code", example = "USD", requiredMode = Schema.RequiredMode.REQUIRED)
        String currency,

        @Schema(description = "Voucher or transaction reference", example = "REC-00001")
        String transactionReference
) {
}
