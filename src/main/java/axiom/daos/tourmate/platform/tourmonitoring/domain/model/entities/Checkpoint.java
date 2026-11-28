package axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities;

import axiom.daos.tourmate.platform.tourmonitoring.domain.exceptions.TourExecutionDomainException;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.GeoLocation;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TimeWindow;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain entity representing a route checkpoint within a {@code TourExecution}.
 */
@Getter
public class Checkpoint {

    private final Long id;
    private final String name;
    private final int sequenceOrder;
    private final GeoLocation location;
    private final TimeWindow timeWindow;
    private boolean reached;
    private Instant reachedAt;
    private long delayMinutes;
    private boolean delayedBeyondTolerance;

    public Checkpoint(
            Long id,
            String name,
            int sequenceOrder,
            GeoLocation location,
            TimeWindow timeWindow
    ) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("Checkpoint id must be greater than 0");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Checkpoint name cannot be null or blank");
        }
        if (sequenceOrder < 1) {
            throw new IllegalArgumentException("Checkpoint sequence order must be at least 1");
        }
        this.id = id;
        this.name = name.trim();
        this.sequenceOrder = sequenceOrder;
        this.location = Objects.requireNonNull(location, "Checkpoint location is required");
        this.timeWindow = Objects.requireNonNull(timeWindow, "Checkpoint time window is required");
        this.reached = false;
        this.reachedAt = null;
        this.delayMinutes = 0L;
        this.delayedBeyondTolerance = false;
    }

    public static Checkpoint reconstitute(
            Long id,
            String name,
            int sequenceOrder,
            GeoLocation location,
            TimeWindow timeWindow,
            boolean reached,
            Instant reachedAt,
            long delayMinutes,
            boolean delayedBeyondTolerance
    ) {
        var checkpoint = new Checkpoint(id, name, sequenceOrder, location, timeWindow);
        checkpoint.reached = reached;
        checkpoint.reachedAt = reachedAt;
        checkpoint.delayMinutes = delayMinutes;
        checkpoint.delayedBeyondTolerance = delayedBeyondTolerance;
        return checkpoint;
    }

    public void markReached(Instant actualArrival, long toleranceMinutes) {
        Objects.requireNonNull(actualArrival, "Reached timestamp cannot be null");
        if (this.reached) {
            throw TourExecutionDomainException.conflict(
                    "CHECKPOINT_ALREADY_REACHED",
                    "Checkpoint %d (%s) has already been reached".formatted(this.id, this.name)
            );
        }
        this.delayMinutes = this.timeWindow.calculateDelayMinutes(actualArrival);
        this.delayedBeyondTolerance = this.timeWindow.isDelayedBeyondTolerance(actualArrival, toleranceMinutes);
        this.reached = true;
        this.reachedAt = actualArrival;
    }
}
