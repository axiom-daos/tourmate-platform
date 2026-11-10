package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates;

import axiom.daos.tourmate.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.valueobjects.SubscriptionStatus;
import lombok.Getter;

import java.util.Date;
import java.util.Objects;

/**
 * Subscription aggregate root.
 *
 * <p>
 * Represents an active, pending, or cancelled commercial contract between an agency
 * and TourMate, determining the platform capabilities unlocked for the agency.
 * </p>
 */
@Getter
public class Subscription extends AbstractDomainAggregateRoot<Subscription> {

    /**
     * Unique identifier for the subscription.
     */
    private Long id;

    /**
     * The agency identifier owning this subscription contract.
     */
    private Long agencyId;

    /**
     * The subscribed plan identifier.
     */
    private Long planId;

    /**
     * Current lifecycle status of the subscription.
     */
    private SubscriptionStatus status;

    /**
     * Timestamp when the subscription was officially activated.
     */
    private Date activatedAt;

    /**
     * Timestamp when the subscription was cancelled, if applicable.
     */
    private Date cancelledAt;

    /**
     * Default constructor for framework initialization.
     */
    public Subscription() {
        this.status = SubscriptionStatus.PENDING;
    }

    /**
     * Creates a new active subscription for an agency.
     *
     * @param agencyId The agency identifier
     * @param planId The selected plan identifier
     */
    public Subscription(Long agencyId, Long planId) {
        this.agencyId = validateId(agencyId, "Agency id");
        this.planId = validateId(planId, "Plan id");
        this.status = SubscriptionStatus.ACTIVE;
        this.activatedAt = new Date();
    }

    /**
     * Reconstitutes a subscription from persistent storage.
     *
     * @param id The persisted identifier
     * @param agencyId The agency identifier
     * @param planId The plan identifier
     * @param status The current lifecycle status
     * @param activatedAt The activation timestamp
     * @param cancelledAt The cancellation timestamp
     * @return Reconstituted Subscription instance
     */
    public static Subscription reconstitute(
            Long id,
            Long agencyId,
            Long planId,
            SubscriptionStatus status,
            Date activatedAt,
            Date cancelledAt
    ) {
        var subscription = new Subscription();
        subscription.id = id;
        subscription.agencyId = agencyId;
        subscription.planId = planId;
        subscription.status = status != null ? status : SubscriptionStatus.ACTIVE;
        subscription.activatedAt = activatedAt;
        subscription.cancelledAt = cancelledAt;
        return subscription;
    }

    /**
     * Cancels the active subscription.
     */
    public void cancel() {
        if (this.status == SubscriptionStatus.CANCELLED) {
            return;
        }
        this.status = SubscriptionStatus.CANCELLED;
        this.cancelledAt = new Date();
    }

    /**
     * Checks if the subscription is currently active.
     *
     * @return {@code true} if status is ACTIVE, {@code false} otherwise
     */
    public boolean isActive() {
        return this.status == SubscriptionStatus.ACTIVE;
    }

    private Long validateId(Long id, String fieldName) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException(fieldName + " must be at least 1");
        }
        return id;
    }
}
