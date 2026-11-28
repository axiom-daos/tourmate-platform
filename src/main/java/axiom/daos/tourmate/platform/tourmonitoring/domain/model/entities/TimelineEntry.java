package axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities;

import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Chronological timeline entry recorded during a {@code TourExecution}.
 */
@Getter
public class TimelineEntry {

    private final Long id;
    private final String eventType;
    private final String description;
    private final Instant occurredAt;

    public TimelineEntry(Long id, String eventType, String description, Instant occurredAt) {
        if (eventType == null || eventType.isBlank()) {
            throw new IllegalArgumentException("Timeline event type is required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Timeline description is required");
        }
        this.id = id;
        this.eventType = eventType.trim();
        this.description = description.trim();
        this.occurredAt = Objects.requireNonNull(occurredAt, "Timeline timestamp is required");
    }
}
