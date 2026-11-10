package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.valueobjects;

/**
 * Enumeration representing the possible states of an agency subscription.
 */
public enum SubscriptionStatus {
    ACTIVE,
    CANCELLED,
    PENDING,
    EXPIRED;

    /**
     * Resolves a subscription status from its textual representation.
     *
     * @param value The string representation
     * @return Corresponding {@link SubscriptionStatus} or {@code ACTIVE} by default
     */
    public static SubscriptionStatus fromValue(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        try {
            return SubscriptionStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
