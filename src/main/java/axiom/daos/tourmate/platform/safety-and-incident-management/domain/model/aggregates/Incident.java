package axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.aggregates;

import axiom.daos.tourmate.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import axiom.daos.tourmate.platform.safety.domain.model.valueobjects.IncidentSeverity;
import axiom.daos.tourmate.platform.safety.domain.model.valueobjects.IncidentStatus;
import org.jspecify.annotations.NullMarked;

import java.util.Date;

@NullMarked
public class Incident extends AbstractDomainAggregateRoot<Incident> {
    private Long id;
    private String uuid;
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

    public Incident(String uuid, Long activeTourId, Long reportedByUserId, String description,
                    Double latitude, Double longitude, Date reportedAt, IncidentStatus status,
                    IncidentSeverity severity, String resolutionNote, String reporterRole) {
        this.uuid = uuid;
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

    // Getters y métodos de negocio para actualizar el estado o resolver
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUuid() { return uuid; }
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

    public void updateDetails(String description, Double latitude, Double longitude,
                              IncidentStatus status, IncidentSeverity severity, String resolutionNote) {
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.severity = severity;
        if (resolutionNote != null) {
            this.resolutionNote = resolutionNote;
        }
    }
}
