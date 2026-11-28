package axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionAlertType;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.GeoLocation;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.RiskLevel;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain entity representing an operational or emergency alert in a {@code TourExecution}.
 */
@Getter
public class Alert {

    private final Long id;
    private final ExecutionAlertType type;
    private final RiskLevel severity;
    private final String message;
    private final Long sourceUserId;
    private final String sourceRole;
    private final GeoLocation location;
    private boolean active;
    private final Instant createdAt;

    public Alert(
            Long id,
            ExecutionAlertType type,
            RiskLevel severity,
            String message,
            Long sourceUserId,
            String sourceRole,
            GeoLocation location,
            boolean active,
            Instant createdAt
    ) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Alert message cannot be null or blank");
        }
        this.id = id;
        this.type = Objects.requireNonNull(type, "Alert type is required");
        this.severity = Objects.requireNonNull(severity, "Alert severity is required");
        this.message = message.trim();
        this.sourceUserId = sourceUserId;
        this.sourceRole = sourceRole;
        this.location = location;
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "Alert timestamp is required");
    }

    public void deactivate() {
        this.active = false;
    }
}
