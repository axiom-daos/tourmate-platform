package axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects;

import org.jspecify.annotations.NullMarked;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Planned arrival time window for a checkpoint during a {@code TourExecution}.
 *
 * @param expectedArrival      Planned arrival timestamp.
 * @param latestAllowedArrival Latest arrival timestamp before delay tolerance is applied.
 */
@NullMarked
public record TimeWindow(Instant expectedArrival, Instant latestAllowedArrival) {

    public TimeWindow {
        Objects.requireNonNull(expectedArrival, "Expected arrival cannot be null");
        Objects.requireNonNull(latestAllowedArrival, "Latest allowed arrival cannot be null");
        if (latestAllowedArrival.isBefore(expectedArrival)) {
            throw new IllegalArgumentException("Latest allowed arrival cannot be before expected arrival");
        }
    }

    public long calculateDelayMinutes(Instant actualArrival) {
        Objects.requireNonNull(actualArrival, "Actual arrival cannot be null");
        if (!actualArrival.isAfter(expectedArrival)) {
            return 0L;
        }
        return Math.max(0L, Duration.between(expectedArrival, actualArrival).toMinutes());
    }

    public boolean isDelayedBeyondTolerance(Instant actualArrival, long toleranceMinutes) {
        Objects.requireNonNull(actualArrival, "Actual arrival cannot be null");
        if (toleranceMinutes < 0) {
            throw new IllegalArgumentException("Tolerance minutes cannot be negative");
        }
        Instant deadline = latestAllowedArrival.plus(Duration.ofMinutes(toleranceMinutes));
        return actualArrival.isAfter(deadline);
    }
}
