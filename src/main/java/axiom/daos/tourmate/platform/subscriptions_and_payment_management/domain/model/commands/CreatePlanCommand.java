package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands;

import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;

/**
 * Command to create a new subscription plan in the commercial catalog.
 *
 * @param name Commercial plan name
 * @param priceAmount Price numerical amount
 * @param priceCurrency 3-letter ISO currency code
 * @param description Detailed feature scope
 * @param maxActiveTours Max simultaneous active tours
 */
@NullMarked
public record CreatePlanCommand(
        String name,
        BigDecimal priceAmount,
        String priceCurrency,
        String description,
        Integer maxActiveTours
) {
    public CreatePlanCommand {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Plan name is required");
        }
        if (priceAmount == null || priceAmount.signum() <= 0) {
            throw new IllegalArgumentException("Price amount must be greater than zero");
        }
        if (priceCurrency == null || priceCurrency.isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
        if (maxActiveTours == null || maxActiveTours < 1) {
            throw new IllegalArgumentException("Max active tours must be at least 1");
        }
    }
}
