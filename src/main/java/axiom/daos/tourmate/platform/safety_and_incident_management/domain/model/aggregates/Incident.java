package axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.aggregates;

import axiom.daos.tourmate.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.valueobjects.IncidentSeverity;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.valueobjects.IncidentStatus;
import org.jspecify.annotations.NullMarked;

import java.util.Date;

@NullMarked
/**
 * Represents an incident reported during a tour, encapsulating details such as the tour ID, reporter ID, description, location, status, severity, and resolution information.
 */
public class Incident extends AbstractDomainAggregateRoot<Incident> {
    private Long id;
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

    /**
     * Constructs a new Incident with the specified details.
     * @param activeTourId
     * @param reportedByUserId
     * @param description
     * @param latitude
     * @param longitude
     * @param reportedAt
     * @param status
     * @param severity
     * @param resolutionNote
     * @param reporterRole
     */

    public Incident(Long activeTourId, Long reportedByUserId, String description,
                    Double latitude, Double longitude, Date reportedAt, IncidentStatus status,
                    IncidentSeverity severity, String resolutionNote, String reporterRole) {
        this.activeTourId = activeTourId;
        this.reportedByUserId = reportedByUserId;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reportedAt = reportedAt;
        this.status = status;
        this.severity = severity;
        this.resolutionNote = resolutionNote != null ? resolutionNote : "";
        this.reporterRole = reporterRole != null ? reporterRole : "TOURIST";
    }

    public static Incident reconstitute(Long id, Long activeTourId, Long reportedByUserId,
                                        String description, Double latitude, Double longitude, Date reportedAt,
                                        IncidentStatus status, IncidentSeverity severity, String resolutionNote,
                                        String reporterRole) {
        var incident = new Incident(activeTourId, reportedByUserId, description, latitude, longitude,
                reportedAt, status, severity, resolutionNote, reporterRole);
        incident.id = id;
        return incident;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getActiveTourId() { return activeTourId; }
    public Long getReportedByUserId() { return reportedByUserId; }
    public String getDescription() { return description; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Date getReportedAt() { return reportedAt; }
    public IncidentStatus getStatus() { return status; }
    public IncidentSeverity getSeverity() { return severity; }
    public String getResolutionNote() { return resolutionNote; }
    public String getReporterRole() { return reporterRole; }

    /**
     * Updates the incident's information with the provided values. If resolutionNote is null, it will not be updated.
     * @param activeTourId
     * @param reportedByUserId
     * @param description
     * @param latitude
     * @param longitude
     * @param reportedAt
     * @param status
     * @param severity
     * @param resolutionNote
     * @param reporterRole
     */
    public void updateInformation(Long activeTourId, Long reportedByUserId, String description,
                                 Double latitude, Double longitude, Date reportedAt, IncidentStatus status,
                                 IncidentSeverity severity, String resolutionNote, String reporterRole) {
        this.activeTourId = activeTourId;
        this.reportedByUserId = reportedByUserId;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reportedAt = reportedAt;
        this.status = status;
        this.severity = severity;
        if (resolutionNote != null) {
            this.resolutionNote = resolutionNote;
        }
        this.reporterRole = reporterRole;
    }
}
