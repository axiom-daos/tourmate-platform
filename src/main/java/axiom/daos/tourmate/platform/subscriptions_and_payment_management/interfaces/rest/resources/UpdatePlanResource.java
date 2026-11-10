package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * REST request resource to update an existing subscription plan.
 */
@Schema(name = "UpdatePlanRequest", description = "Payload for updating an existing subscription plan")
public record UpdatePlanResource(
        @Schema(description = "Commercial plan name", example = "Pro Trekker Plus", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Plan monthly price amount", example = "89.00", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal priceAmount,

        @Schema(description = "ISO currency code", example = "USD", requiredMode = Schema.RequiredMode.REQUIRED)
        String priceCurrency,

        @Schema(description = "Commercial plan description", example = "Updated mid-tier package with 25 tours quota")
        String description,

        @Schema(description = "Maximum active tours allowed simultaneously", example = "25", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer maxActiveTours
) {
}
