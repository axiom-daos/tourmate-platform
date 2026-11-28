package axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects;

/**
 * Operational risk level of a {@code TourExecution}.
 */
public enum RiskLevel {
    LOW,
    MODERATE,
    HIGH,
    CRITICAL;

    public RiskLevel escalate() {
        return switch (this) {
            case LOW -> MODERATE;
            case MODERATE -> HIGH;
            case HIGH, CRITICAL -> CRITICAL;
        };
    }

    public RiskLevel max(RiskLevel other) {
        if (other == null) {
            return this;
        }
        return this.ordinal() >= other.ordinal() ? this : other;
    }
}
