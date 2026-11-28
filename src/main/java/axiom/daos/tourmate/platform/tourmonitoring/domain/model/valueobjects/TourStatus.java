package axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects;

/**
 * Lifecycle state machine for a {@code TourExecution}.
 *
 * <p>Valid transitions: {@code SCHEDULED -> IN_PROGRESS <-> PAUSED -> COMPLETED | ABORTED}
 * (plus {@code SCHEDULED -> ABORTED}).</p>
 */
public enum TourStatus {
    SCHEDULED,
    IN_PROGRESS,
    PAUSED,
    COMPLETED,
    ABORTED;

    public boolean canTransitionTo(TourStatus target) {
        if (target == null || target == this) {
            return false;
        }
        return switch (this) {
            case SCHEDULED -> target == IN_PROGRESS || target == ABORTED;
            case IN_PROGRESS -> target == PAUSED || target == COMPLETED || target == ABORTED;
            case PAUSED -> target == IN_PROGRESS || target == ABORTED;
            case COMPLETED, ABORTED -> false;
        };
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == ABORTED;
    }

    public boolean isActive() {
        return this == SCHEDULED || this == IN_PROGRESS || this == PAUSED;
    }
}
