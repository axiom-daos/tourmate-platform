package axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.entities;

import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * JPA persistence entity for subscription plans.
 */
@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "billing_cycle_days", nullable = false)
    private Integer billingCycleDays;

    @Column(name = "max_tours", nullable = false)
    private Integer maxTours;

    @Column(name = "max_tourists_per_tour", nullable = false)
    private Integer maxTouristsPerTour;

    @Column(name = "is_custom_branding_allowed", nullable = false)
    private Boolean isCustomBrandingAllowed;

    @Column(name = "is_priority_support_included", nullable = false)
    private Boolean isPrioritySupportIncluded;
}
