package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.valueobjects;

/**
 * Enumeration representing the status of a subscription payment transaction.
 */
public enum PaymentStatus {
    PAID,
    PENDING,
    REJECTED;

    /**
     * Resolves a payment status from its textual representation.
     *
     * @param value The string representation
     * @return Corresponding {@link PaymentStatus} or {@code PAID} by default
     */
    public static PaymentStatus fromValue(String value) {
        if (value == null || value.isBlank()) {
            return PAID;
        }
        try {
            return PaymentStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PAID;
        }
    }
}
