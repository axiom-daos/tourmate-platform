package axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.entities;

import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.valueobjects.IncidentSeverity;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.valueobjects.IncidentStatus;
import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "incidents")
/**
 * Represents the JPA entity for an incident reported during a tour, mapping to the "incidents" table in the database. It includes fields for tour ID, reporter ID, description, location, status, severity, and resolution information.
 */
public class IncidentJpaEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "active_tour_id", nullable = false)
    private Long activeTourId;

    @Column(name = "reported_by_user_id", nullable = false)
    private Long reportedByUserId;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "reported_at", nullable = false)
    private Date reportedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentSeverity severity;

    @Column(name = "resolution_note")
    private String resolutionNote;

    @Column(name = "reporter_role", nullable = false)
    private String reporterRole;
}