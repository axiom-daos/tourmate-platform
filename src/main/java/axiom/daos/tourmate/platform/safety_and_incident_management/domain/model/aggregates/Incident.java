package axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.aggregates;

import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.valueobjects.IncidentSeverity;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.valueobjects.IncidentStatus;
import axiom.daos.tourmate.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * Aggregate root representing an incident reported during a tour.
 */
@NullMarked
public class Incident extends AbstractDomainAggregateRoot<Incident> {

    private static final int MAX_DESCRIPTION_LENGTH = 1000;
    private static final int MAX_RESOLUTION_NOTE_LENGTH = 1000;
    private static final int MIN_RESOLUTION_NOTE_LENGTH = 5;
    private static final Set<String> ALLOWED_REPORTER_ROLES = Set.of("TOURIST", "GUIDE");

    private @Nullable Long id;
    private UUID uuid;
    private Long activeTourId;
    private Long reportedByUserId;
    private String description;
    private Double latitude;
    private Double longitude;
    private Date reportedAt;
    private IncidentStatus status;
    private IncidentSeverity severity;
    private String resolutionNote;
    private String reporterRole;

    public Incident(
            Long activeTourId,
            Long reportedByUserId,
            String description,
            Double latitude,
            Double longitude,
            Date reportedAt,
            IncidentStatus status,
            IncidentSeverity severity,
            @Nullable String resolutionNote,
            String reporterRole
    ) {
        this(
                UUID.randomUUID(),
                activeTourId,
                reportedByUserId,
                description,
                latitude,
                longitude,
                reportedAt,
                status,
                severity,
                resolutionNote,
                reporterRole
        );
    }

    public Incident(
            UUID uuid,
            Long activeTourId,
            Long reportedByUserId,
            String description,
            Double latitude,
            Double longitude,
            Date reportedAt,
            IncidentStatus status,
            IncidentSeverity severity,
            @Nullable String resolutionNote,
            String reporterRole
    ) {
        validateAndAssign(
                uuid,
                activeTourId,
                reportedByUserId,
                description,
                latitude,
                longitude,
                reportedAt,
                status,
                severity,
                resolutionNote,
                reporterRole,
                true
        );
    }

    public static Incident reconstitute(
            @Nullable Long id,
            @Nullable UUID uuid,
            Long activeTourId,
            Long reportedByUserId,
            String description,
            Double latitude,
            Double longitude,
            Date reportedAt,
            IncidentStatus status,
            IncidentSeverity severity,
            @Nullable String resolutionNote,
            @Nullable String reporterRole
    ) {
        var resolvedUuid = uuid != null ? uuid : UUID.randomUUID();
        var resolvedRole = reporterRole != null && !reporterRole.isBlank() ? reporterRole : "TOURIST";
        var incident = new Incident(
                resolvedUuid,
                activeTourId,
                reportedByUserId,
                description,
                latitude,
                longitude,
                reportedAt,
                status,
                severity,
                resolutionNote,
                resolvedRole,
                false
        );
        incident.id = id;
        return incident;
    }

    private Incident(
            UUID uuid,
            Long activeTourId,
            Long reportedByUserId,
            String description,
            Double latitude,
            Double longitude,
            Date reportedAt,
            IncidentStatus status,
            IncidentSeverity severity,
            @Nullable String resolutionNote,
            String reporterRole,
            boolean enforceResolutionRuleOnInit
    ) {
        validateAndAssign(
                uuid,
                activeTourId,
                reportedByUserId,
                description,
                latitude,
                longitude,
                reportedAt,
                status,
                severity,
                resolutionNote,
                reporterRole,
                enforceResolutionRuleOnInit
        );
    }

    public @Nullable Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Long getActiveTourId() {
        return activeTourId;
    }

    public Long getReportedByUserId() {
        return reportedByUserId;
    }

    public String getDescription() {
        return description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Date getReportedAt() {
        return new Date(reportedAt.getTime());
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public IncidentSeverity getSeverity() {
        return severity;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }

    public String getReporterRole() {
        return reporterRole;
    }

    public boolean isResolvedOrClosed() {
        return this.status == IncidentStatus.RESOLVED || this.status == IncidentStatus.CLOSED;
    }

    /**
     * Marks the incident as IN_REVIEW if it is currently OPEN.
     */
    public void startReview() {
        if (this.status == IncidentStatus.RESOLVED || this.status == IncidentStatus.CLOSED) {
            throw new IllegalStateException("Resolved or closed incidents cannot move back to IN_REVIEW");
        }
        this.status = IncidentStatus.IN_REVIEW;
    }

    /**
     * Resolves the incident requiring a meaningful resolution note (at least 5 characters).
     */
    public void resolve(@Nullable String resolutionNote) {
        if (this.status == IncidentStatus.CLOSED) {
            throw new IllegalStateException("Closed incidents cannot be resolved again");
        }
        String trimmedNote = resolutionNote != null ? resolutionNote.trim() : "";
        validateResolutionNoteForResolution(trimmedNote);
        this.resolutionNote = trimmedNote;
        this.status = IncidentStatus.RESOLVED;
    }

    /**
     * Closes the incident once resolved (or with a valid resolution note).
     */
    public void close(@Nullable String resolutionNote) {
        String trimmedNote = resolutionNote != null && !resolutionNote.isBlank()
                ? resolutionNote.trim()
                : this.resolutionNote;
        validateResolutionNoteForResolution(trimmedNote);
        this.resolutionNote = trimmedNote;
        this.status = IncidentStatus.CLOSED;
    }

    /**
     * Updates the incident's details while preserving its identity and UUID.
     */
    public void updateInformation(
            Long activeTourId,
            Long reportedByUserId,
            String description,
            Double latitude,
            Double longitude,
            Date reportedAt,
            IncidentStatus status,
            IncidentSeverity severity,
            @Nullable String resolutionNote,
            String reporterRole
    ) {
        if (this.status == IncidentStatus.CLOSED && status != IncidentStatus.CLOSED) {
            throw new IllegalStateException("Closed incidents cannot be reopened or modified to another status");
        }

        String resolvedNote = resolutionNote != null ? resolutionNote.trim() : this.resolutionNote;
        validateAndAssign(
                this.uuid,
                activeTourId,
                reportedByUserId,
                description,
                latitude,
                longitude,
                reportedAt,
                status,
                severity,
                resolvedNote,
                reporterRole,
                true
        );
    }

    public void publishEvent(Object event) {
        registerDomainEvent(event);
    }

    private void validateAndAssign(
            @Nullable UUID uuid,
            Long activeTourId,
            Long reportedByUserId,
            String description,
            Double latitude,
            Double longitude,
            Date reportedAt,
            IncidentStatus status,
            IncidentSeverity severity,
            @Nullable String resolutionNote,
            String reporterRole,
            boolean enforceResolutionRule
    ) {
        if (activeTourId == null || activeTourId <= 0) {
            throw new IllegalArgumentException("Active tour ID must be a positive number");
        }
        if (reportedByUserId == null || reportedByUserId <= 0) {
            throw new IllegalArgumentException("Reported by user ID must be a positive number");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Incident description cannot be null or blank");
        }
        String trimmedDescription = description.trim();
        if (trimmedDescription.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    "Incident description cannot exceed %d characters".formatted(MAX_DESCRIPTION_LENGTH)
            );
        }
        validateCoordinates(latitude, longitude);
        if (reportedAt == null) {
            throw new IllegalArgumentException("Reported date cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Incident status cannot be null");
        }
        if (severity == null) {
            throw new IllegalArgumentException("Incident severity cannot be null");
        }

        String normalizedRole = normalizeReporterRole(reporterRole);
        String trimmedNote = resolutionNote != null ? resolutionNote.trim() : "";
        if (trimmedNote.length() > MAX_RESOLUTION_NOTE_LENGTH) {
            throw new IllegalArgumentException(
                    "Resolution note cannot exceed %d characters".formatted(MAX_RESOLUTION_NOTE_LENGTH)
            );
        }
        if (enforceResolutionRule && (status == IncidentStatus.RESOLVED || status == IncidentStatus.CLOSED)) {
            validateResolutionNoteForResolution(trimmedNote);
        }

        this.uuid = uuid != null ? uuid : UUID.randomUUID();
        this.activeTourId = activeTourId;
        this.reportedByUserId = reportedByUserId;
        this.description = trimmedDescription;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reportedAt = new Date(reportedAt.getTime());
        this.status = status;
        this.severity = severity;
        this.resolutionNote = trimmedNote;
        this.reporterRole = normalizedRole;
    }

    private static void validateResolutionNoteForResolution(String note) {
        if (note.isBlank() || note.length() < MIN_RESOLUTION_NOTE_LENGTH) {
            throw new IllegalArgumentException(
                    "Resolution note must be at least %d characters when resolving or closing an incident"
                            .formatted(MIN_RESOLUTION_NOTE_LENGTH)
            );
        }
    }

    private static void validateCoordinates(Double latitude, Double longitude) {
        if (latitude == null || latitude.isNaN() || latitude.isInfinite() || latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("Latitude must be a finite number between -90 and 90");
        }
        if (longitude == null || longitude.isNaN() || longitude.isInfinite() || longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Longitude must be a finite number between -180 and 180");
        }
    }

    private static String normalizeReporterRole(@Nullable String reporterRole) {
        if (reporterRole == null || reporterRole.isBlank()) {
            throw new IllegalArgumentException("Reporter role cannot be null or blank");
        }
        String normalized = reporterRole.trim().toUpperCase();
        if (!ALLOWED_REPORTER_ROLES.contains(normalized)) {
            throw new IllegalArgumentException(
                    "Invalid reporter role: %s. Allowed values: TOURIST, GUIDE".formatted(reporterRole)
            );
        }
        return normalized;
    }
}