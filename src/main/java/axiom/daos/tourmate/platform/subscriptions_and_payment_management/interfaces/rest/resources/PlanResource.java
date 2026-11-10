package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * REST response resource for a subscription plan.
 */
@Schema(name = "PlanResponse", description = "TourMate subscription plan details")
public record PlanResource(
        @Schema(description = "Plan unique identifier", example = "1")
        Long id,

        @Schema(description = "Commercial plan name", example = "Pro Trekker")
        String name,

        @Schema(description = "Plan monthly price amount", example = "79.00")
        BigDecimal priceAmount,

        @Schema(description = "ISO currency code", example = "USD")
        String priceCurrency,

        @Schema(description = "Commercial plan description", example = "Mid-tier package for growing tour operators")
        String description,

        @Schema(description = "Maximum active tours allowed simultaneously", example = "20")
        Integer maxActiveTours
) {
}
