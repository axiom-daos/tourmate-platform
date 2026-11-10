package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * REST request resource to create a subscription plan.
 */
@Schema(name = "CreatePlanRequest", description = "Payload for creating a new subscription plan")
public record CreatePlanResource(
        @Schema(description = "Commercial plan name", example = "Enterprise Expedition", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Plan monthly price amount", example = "199.00", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal priceAmount,

        @Schema(description = "ISO currency code", example = "USD", requiredMode = Schema.RequiredMode.REQUIRED)
        String priceCurrency,

        @Schema(description = "Commercial plan description", example = "Unlimited active tours and dedicated enterprise support")
        String description,

        @Schema(description = "Maximum active tours allowed simultaneously", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer maxActiveTours
) {
}
