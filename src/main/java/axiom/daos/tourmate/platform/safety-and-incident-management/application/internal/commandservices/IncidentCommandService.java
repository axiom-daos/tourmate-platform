package axiom.daos.tourmate.platform.safety_and_incident_management.application.internal.commandservices;

import axiom.daos.tourmate.platform.safety.domain.model.aggregates.Incident;
import axiom.daos.tourmate.platform.safety.domain.model.repositories.IncidentRepository;
import axiom.daos.tourmate.platform.safety.domain.model.valueobjects.IncidentSeverity;
import axiom.daos.tourmate.platform.safety.domain.model.valueobjects.IncidentStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@NullMarked
@Service
public class IncidentCommandService {

    private final IncidentRepository incidentRepository;

    public IncidentCommandService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Transactional
    public Incident createIncident(
            @Nullable String uuid,
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
        String generatedUuid = (uuid == null || uuid.isBlank())
                ? "off-" + UUID.randomUUID().toString().substring(0, 7)
                : uuid;

        Incident incident = new Incident(
                generatedUuid,
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
    public boolean deleteIncident(Long id) {
        if (!incidentRepository.existsById(id)) {
            return false;
        }
        incidentRepository.deleteById(id);
        return true;
    }
}