package axiom.daos.tourmate.platform.safety_and_incident_management.application.internal.commandservices;

import axiom.daos.tourmate.platform.iam.interfaces.acl.IamContextFacade;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.aggregates.Incident;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.valueobjects.IncidentSeverity;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.valueobjects.IncidentStatus;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.persistence.IncidentRepository;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Application service handling write commands and business rules for incidents.
 */
@NullMarked
@Service
public class IncidentCommandService {

    private final IncidentRepository incidentRepository;
    private final @Nullable IamContextFacade iamContextFacade;

    public IncidentCommandService(IncidentRepository incidentRepository) {
        this(incidentRepository, null);
    }

    @Autowired
    public IncidentCommandService(
            IncidentRepository incidentRepository,
            @Nullable IamContextFacade iamContextFacade
    ) {
        this.incidentRepository = incidentRepository;
        this.iamContextFacade = iamContextFacade;
    }

    @Transactional
    public Result<Incident, ApplicationError> createIncident(
            @Nullable Long activeTourId,
            @Nullable Long reportedByUserId,
            @Nullable String description,
            @Nullable Double latitude,
            @Nullable Double longitude,
            @Nullable Date reportedAt,
            @Nullable IncidentStatus status,
            @Nullable IncidentSeverity severity,
            @Nullable String resolutionNote,
            @Nullable String reporterRole
    ) {
        if (activeTourId == null || activeTourId <= 0) {
            return Result.failure(ApplicationError.validationError("activeTourId", "Active tour ID must be a positive number"));
        }
        if (reportedByUserId == null || reportedByUserId <= 0) {
            return Result.failure(ApplicationError.validationError("reportedByUserId", "Reported by user ID must be a positive number"));
        }

        var userValidationError = validateReporterExists(reportedByUserId);
        if (userValidationError != null) {
            return Result.failure(userValidationError);
        }

        IncidentStatus resolvedStatus = status != null ? status : IncidentStatus.OPEN;
        IncidentSeverity resolvedSeverity = severity != null ? severity : IncidentSeverity.MEDIUM;
        String resolvedRole = reporterRole != null && !reporterRole.isBlank() ? reporterRole : "TOURIST";

        try {
            Incident incident = new Incident(
                    activeTourId,
                    reportedByUserId,
                    description != null ? description : "",
                    latitude,
                    longitude,
                    reportedAt != null ? reportedAt : new Date(),
                    resolvedStatus,
                    resolvedSeverity,
                    resolutionNote,
                    resolvedRole
            );

            Incident saved = incidentRepository.save(incident);
            return Result.success(saved);
        } catch (IllegalArgumentException ex) {
            if (resolvedStatus == IncidentStatus.RESOLVED || resolvedStatus == IncidentStatus.CLOSED) {
                return Result.failure(ApplicationError.businessRuleViolation("incident-resolution", ex.getMessage()));
            }
            return Result.failure(ApplicationError.validationError("incident", ex.getMessage()));
        }
    }

    @Transactional
    public Result<Incident, ApplicationError> updateIncident(
            @Nullable Long id,
            @Nullable Long activeTourId,
            @Nullable Long reportedByUserId,
            @Nullable String description,
            @Nullable Double latitude,
            @Nullable Double longitude,
            @Nullable Date reportedAt,
            @Nullable IncidentStatus status,
            @Nullable IncidentSeverity severity,
            @Nullable String resolutionNote,
            @Nullable String reporterRole
    ) {
        if (id == null || id <= 0) {
            return Result.failure(ApplicationError.validationError("id", "Incident ID must be a positive number"));
        }

        if (activeTourId != null && activeTourId <= 0) {
            return Result.failure(ApplicationError.validationError("activeTourId", "Active tour ID must be a positive number"));
        }
        if (reportedByUserId != null && reportedByUserId <= 0) {
            return Result.failure(ApplicationError.validationError("reportedByUserId", "Reported by user ID must be a positive number"));
        }

        var existingOpt = incidentRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Incident", id.toString()));
        }

        if (reportedByUserId != null && reportedByUserId > 0) {
            var userValidationError = validateReporterExists(reportedByUserId);
            if (userValidationError != null) {
                return Result.failure(userValidationError);
            }
        }

        Incident existingIncident = existingOpt.get();
        Date resolvedReportedAt = reportedAt != null ? reportedAt : existingIncident.getReportedAt();
        IncidentStatus resolvedStatus = status != null ? status : existingIncident.getStatus();
        IncidentSeverity resolvedSeverity = severity != null ? severity : existingIncident.getSeverity();
        String resolvedRole = reporterRole != null && !reporterRole.isBlank()
                ? reporterRole
                : existingIncident.getReporterRole();

        try {
            existingIncident.updateInformation(
                    activeTourId != null ? activeTourId : existingIncident.getActiveTourId(),
                    reportedByUserId != null ? reportedByUserId : existingIncident.getReportedByUserId(),
                    description != null ? description : existingIncident.getDescription(),
                    latitude != null ? latitude : existingIncident.getLatitude(),
                    longitude != null ? longitude : existingIncident.getLongitude(),
                    resolvedReportedAt,
                    resolvedStatus,
                    resolvedSeverity,
                    resolutionNote,
                    resolvedRole
            );

            Incident saved = incidentRepository.save(existingIncident);
            return Result.success(saved);
        } catch (IllegalStateException ex) {
            return Result.failure(ApplicationError.businessRuleViolation("incident-status-transition", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            if (resolvedStatus == IncidentStatus.RESOLVED || resolvedStatus == IncidentStatus.CLOSED) {
                return Result.failure(ApplicationError.businessRuleViolation("incident-resolution", ex.getMessage()));
            }
            return Result.failure(ApplicationError.validationError("incident", ex.getMessage()));
        }
    }

    @Transactional
    public Result<Void, ApplicationError> deleteIncident(@Nullable Long id) {
        if (id == null || id <= 0) {
            return Result.failure(ApplicationError.validationError("id", "Incident ID must be a positive number"));
        }
        if (!incidentRepository.existsById(id)) {
            return Result.failure(ApplicationError.notFound("Incident", id.toString()));
        }
        incidentRepository.deleteById(id);
        return Result.success(null);
    }

    private @Nullable ApplicationError validateReporterExists(Long reportedByUserId) {
        if (iamContextFacade != null && !iamContextFacade.existsUserById(reportedByUserId)) {
            return ApplicationError.notFound("User", reportedByUserId.toString());
        }
        return null;
    }
}