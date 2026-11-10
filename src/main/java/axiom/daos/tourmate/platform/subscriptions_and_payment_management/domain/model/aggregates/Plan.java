package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates;

import axiom.daos.tourmate.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Money;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

/**
 * Plan aggregate root.
 *
 * <p>
 * Represents a commercial subscription plan offering tour operators different quotas,
 * monitoring capabilities, and support tiers.
 * </p>
 */
@Getter
public class Plan extends AbstractDomainAggregateRoot<Plan> {

    /**
     * Unique identifier for the plan.
     */
    private Long id;

    /**
     * Commercial display name for the plan.
     */
    private String name;

    /**
     * Monetary price of the plan per monthly billing cycle.
     */
    private Money price;

    /**
     * Commercial description of the plan features and scope.
     */
    private String description;

    /**
     * Maximum number of concurrent active tours permitted for this tier.
     */
    private Integer maxActiveTours;

    /**
     * Default constructor initializing default values.
     */
    public Plan() {
        this.name = Strings.EMPTY;
        this.description = Strings.EMPTY;
        this.maxActiveTours = 1;
    }

    /**
     * Constructor for creating a new Plan instance.
     *
     * @param name The plan name
     * @param price The monetary monthly price
     * @param description Plan scope description
     * @param maxActiveTours Maximum active tours quota
     */
    public Plan(String name, Money price, String description, Integer maxActiveTours) {
        this.name = validateName(name);
        this.price = Objects.requireNonNull(price, "Price is required");
        this.description = description != null ? description.trim() : Strings.EMPTY;
        this.maxActiveTours = validateMaxActiveTours(maxActiveTours);
    }

    /**
     * Reconstitutes an existing Plan from persistence storage.
     *
     * @param id The persisted unique identifier
     * @param name The commercial name
     * @param price The persisted price
     * @param description Scope description
     * @param maxActiveTours Tours quota
     * @return Reconstituted Plan aggregate root
     */
    public static Plan reconstitute(Long id, String name, Money price, String description, Integer maxActiveTours) {
        var plan = new Plan(name, price, description, maxActiveTours);
        plan.id = id;
        return plan;
    }

    /**
     * Updates the plan information.
     *
     * @param name The new commercial name
     * @param price The new monetary price
     * @param description The updated description
     * @param maxActiveTours The updated tour quota
     * @return The updated Plan instance
     */
    public Plan updateInformation(String name, Money price, String description, Integer maxActiveTours) {
        this.name = validateName(name);
        this.price = Objects.requireNonNull(price, "Price is required");
        this.description = description != null ? description.trim() : Strings.EMPTY;
        this.maxActiveTours = validateMaxActiveTours(maxActiveTours);
        return this;
    }

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Plan name cannot be null or blank");
        }
        return name.trim();
    }

    private Integer validateMaxActiveTours(Integer maxActiveTours) {
        if (maxActiveTours == null || maxActiveTours < 1) {
            throw new IllegalArgumentException("Max active tours must be at least 1");
        }
        return maxActiveTours;
    }
}
