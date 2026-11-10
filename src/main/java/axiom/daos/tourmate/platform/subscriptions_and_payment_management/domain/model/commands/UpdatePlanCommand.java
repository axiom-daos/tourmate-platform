package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands;

import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;

/**
 * Command to update an existing plan in the catalog.
 *
 * @param planId Unique identifier of the plan to update
 * @param name Commercial plan name
 * @param priceAmount Price numerical amount
 * @param priceCurrency 3-letter ISO currency code
 * @param description Detailed feature scope
 * @param maxActiveTours Max simultaneous active tours
 */
@NullMarked
public record UpdatePlanCommand(
        Long planId,
        String name,
        BigDecimal priceAmount,
        String priceCurrency,
        String description,
        Integer maxActiveTours
) {
    public UpdatePlanCommand {
        if (planId == null || planId < 1) {
            throw new IllegalArgumentException("Plan id must be at least 1");
        }
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
