package axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities;

import axiom.daos.tourmate.platform.tourmonitoring.domain.exceptions.TourExecutionDomainException;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionIncidentSeverity;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionIncidentStatus;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.GeoLocation;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain entity representing an incident reported during a {@code TourExecution}.
 */
@Getter
public class Incident {

    private Long id;
    private final String type;
    private final ExecutionIncidentSeverity severity;
    private final String description;
    private final GeoLocation location;
    private final Long reportedById;
    private final String reportedByRole;
    private ExecutionIncidentStatus status;
    private String resolutionNotes;
    private final Instant reportedAt;
    private Instant resolvedAt;

    public Incident(
            Long id,
            String type,
            ExecutionIncidentSeverity severity,
            String description,
            GeoLocation location,
            Long reportedById,
            String reportedByRole,
            Instant reportedAt
    ) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Incident type is required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Incident description is required");
        }
        if (reportedById == null || reportedById < 1) {
            throw new IllegalArgumentException("Reporter ID must be greater than 0");
        }
        this.id = id;
        this.type = type.trim();
        this.severity = Objects.requireNonNull(severity, "Incident severity is required");
        this.description = description.trim();
        this.location = location;
        this.reportedById = reportedById;
        this.reportedByRole = reportedByRole != null && !reportedByRole.isBlank() ? reportedByRole.trim() : "GUIDE";
        this.status = ExecutionIncidentStatus.OPEN;
        this.resolutionNotes = null;
        this.reportedAt = Objects.requireNonNull(reportedAt, "Reported timestamp is required");
        this.resolvedAt = null;
    }

    public static Incident reconstitute(
            Long id,
            String type,
            ExecutionIncidentSeverity severity,
            String description,
            GeoLocation location,
            Long reportedById,
            String reportedByRole,
            ExecutionIncidentStatus status,
            String resolutionNotes,
            Instant reportedAt,
            Instant resolvedAt
    ) {
        var incident = new Incident(id, type, severity, description, location, reportedById, reportedByRole, reportedAt);
        incident.status = Objects.requireNonNull(status, "Incident status is required");
        incident.resolutionNotes = resolutionNotes;
        incident.resolvedAt = resolvedAt;
        return incident;
    }

    public void resolve(String notes, Instant resolvedTimestamp) {
        if (this.status == ExecutionIncidentStatus.RESOLVED) {
            throw TourExecutionDomainException.conflict(
                    "INCIDENT_ALREADY_RESOLVED",
                    "Incident %s is already resolved".formatted(this.id != null ? this.id : this.type)
            );
        }
        if (notes == null || notes.isBlank()) {
            throw new IllegalArgumentException("Resolution notes are required");
        }
        this.status = ExecutionIncidentStatus.RESOLVED;
        this.resolutionNotes = notes.trim();
        this.resolvedAt = Objects.requireNonNull(resolvedTimestamp, "Resolved timestamp is required");
    }

    public boolean isUnresolvedCritical() {
        return this.status == ExecutionIncidentStatus.OPEN
                && this.severity == ExecutionIncidentSeverity.CRITICAL;
    }

    public boolean isOpen() {
        return this.status == ExecutionIncidentStatus.OPEN;
    }
}
