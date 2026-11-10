package axiom.daos.tourmate.platform.safety_and_incident_management.application.internal.commandservices;

import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.aggregates.Incident;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.persistence.IncidentRepository;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.valueobjects.IncidentSeverity;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.valueobjects.IncidentStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@NullMarked
@Service
/**
 * Service class for handling commands related to incidents, such as creating, updating, and deleting incidents.
 */
public class IncidentCommandService {

    private final IncidentRepository incidentRepository;

    public IncidentCommandService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Transactional
    /**
     *
     * Creates a new incident with the provided information and saves it to the repository.
     * If reportedAt is null, it will be set to the current date and time.
     * If status is null, it will be set to OPEN.
     * If severity is null, it will be set to MEDIUM.
     * If reporterRole is null, it will be set to "TOURIST".
     */
    public Incident createIncident(
            Long activeTourId,
            Long reportedByUserId,
            String description,
            Double latitude,
            Double longitude,
            @Nullable Date reportedAt,
            @Nullable IncidentStatus status,
            @Nullable IncidentSeverity severity,
            @Nullable String resolutionNote,
            @Nullable String reporterRole
    ) {
        Incident incident = new Incident(
                activeTourId,
                reportedByUserId,
                description,
                latitude,
                longitude,
                reportedAt != null ? reportedAt : new Date(),
                status != null ? status : IncidentStatus.OPEN,
                severity != null ? severity : IncidentSeverity.MEDIUM,
                resolutionNote,
                reporterRole != null ? reporterRole : "TOURIST"
        );

        return incidentRepository.save(incident);
    }

    @Transactional
    /**
     * Updates an existing incident with the provided information.
     */
    public Optional<Incident> updateIncident(
            Long id,
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
        return incidentRepository.findById(id).map(existingIncident -> {
            existingIncident.updateInformation(
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
            return incidentRepository.save(existingIncident);
        });
    }

    @Transactional

    /**
     * Deletes an incident by its ID.
     *
     * @param id the ID of the incident to delete
     * @return true if the incident was deleted, false if it did not exist
     */
    public boolean deleteIncident(Long id) {
        if (!incidentRepository.existsById(id)) {
            return false;
        }
        incidentRepository.deleteById(id);
        return true;
    }
}