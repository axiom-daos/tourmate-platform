package axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest;

import axiom.daos.tourmate.platform.safety_and_incident_management.application.internal.commandservices.IncidentCommandService;
import axiom.daos.tourmate.platform.safety_and_incident_management.application.internal.queryservices.IncidentQueryService;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.valueobjects.IncidentSeverity;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.valueobjects.IncidentStatus;
import axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest.resources.CreateIncidentResource;
import axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest.resources.IncidentResource;
import axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest.resources.UpdateIncidentResource;
import axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest.transform.IncidentResourceAssembler;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * REST controller exposing CRUD endpoints for tour safety incidents.
 */
@NullMarked
@RestController
@RequestMapping("/api/v1/incidents")
@Tag(name = "Incidents", description = "Available Incident Management CRUD Endpoints")
public class IncidentsController {

    private final IncidentCommandService incidentCommandService;
    private final IncidentQueryService incidentQueryService;

    public IncidentsController(
            IncidentCommandService incidentCommandService,
            IncidentQueryService incidentQueryService
    ) {
        this.incidentCommandService = incidentCommandService;
        this.incidentQueryService = incidentQueryService;
    }

    @GetMapping
    @Operation(summary = "Get all incidents", description = "Retrieves all registered tour incidents.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incidents retrieved successfully")
    })
    public ResponseEntity<List<IncidentResource>> getAllIncidents() {
        var incidents = incidentQueryService.getAllIncidents();
        var resources = incidents.stream()
                .map(IncidentResourceAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get incident by ID", description = "Retrieves an incident by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incident found"),
            @ApiResponse(responseCode = "404", description = "Incident not found")
    })
    public ResponseEntity<IncidentResource> getIncidentById(@PathVariable Long id) {
        return incidentQueryService.getIncidentById(id)
                .map(IncidentResourceAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create incident", description = "Creates a new safety incident.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Incident created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid incident data"),
            @ApiResponse(responseCode = "404", description = "Referenced resource not found")
    })
    public ResponseEntity<?> createIncident(@Valid @RequestBody CreateIncidentResource resource) {
        var statusResult = parseEnumOrNull(resource.status(), IncidentStatus.class, "status");
        if (statusResult.isFailure()) {
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                    ((Result.Failure<@Nullable IncidentStatus, ApplicationError>) statusResult).error()
            );
        }

        var severityResult = parseEnumOrNull(resource.severity(), IncidentSeverity.class, "severity");
        if (severityResult.isFailure()) {
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                    ((Result.Failure<@Nullable IncidentSeverity, ApplicationError>) severityResult).error()
            );
        }

        var result = incidentCommandService.createIncident(
                resource.activeTourId(),
                resource.reportedByUserId(),
                resource.description(),
                resource.latitude(),
                resource.longitude(),
                resource.reportedAt(),
                ((Result.Success<@Nullable IncidentStatus, ApplicationError>) statusResult).value(),
                ((Result.Success<@Nullable IncidentSeverity, ApplicationError>) severityResult).value(),
                resource.resolutionNote(),
                resource.reporterRole()
        );

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                IncidentResourceAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update incident", description = "Updates an existing incident by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incident updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid incident data"),
            @ApiResponse(responseCode = "404", description = "Incident not found")
    })
    public ResponseEntity<?> updateIncident(
            @PathVariable Long id,
            @Valid @RequestBody UpdateIncidentResource resource
    ) {
        var statusResult = parseEnumOrNull(resource.status(), IncidentStatus.class, "status");
        if (statusResult.isFailure()) {
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                    ((Result.Failure<@Nullable IncidentStatus, ApplicationError>) statusResult).error()
            );
        }

        var severityResult = parseEnumOrNull(resource.severity(), IncidentSeverity.class, "severity");
        if (severityResult.isFailure()) {
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                    ((Result.Failure<@Nullable IncidentSeverity, ApplicationError>) severityResult).error()
            );
        }

        var result = incidentCommandService.updateIncident(
                id,
                resource.activeTourId(),
                resource.reportedByUserId(),
                resource.description(),
                resource.latitude(),
                resource.longitude(),
                resource.reportedAt(),
                ((Result.Success<@Nullable IncidentStatus, ApplicationError>) statusResult).value(),
                ((Result.Success<@Nullable IncidentSeverity, ApplicationError>) severityResult).value(),
                resource.resolutionNote(),
                resource.reporterRole()
        );

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                IncidentResourceAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete incident", description = "Deletes an existing incident by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Incident deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Incident not found")
    })
    public ResponseEntity<?> deleteIncident(@PathVariable Long id) {
        var result = incidentCommandService.deleteIncident(id);
        return switch (result) {
            case Result.Success<Void, ApplicationError> _ -> ResponseEntity.noContent().build();
            case Result.Failure<Void, ApplicationError> failure ->
                    ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(failure.error());
        };
    }

    private static <E extends Enum<E>> Result<@Nullable E, ApplicationError> parseEnumOrNull(
            @Nullable String rawValue,
            Class<E> enumClass,
            String fieldName
    ) {
        if (rawValue == null || rawValue.isBlank()) {
            return Result.success(null);
        }
        String normalized = rawValue.trim().toUpperCase();
        for (E constant : enumClass.getEnumConstants()) {
            if (constant.name().equals(normalized)) {
                return Result.success(constant);
            }
        }
        String allowedValues = String.join(", ", Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toList());
        return Result.failure(
                ApplicationError.validationError(
                        fieldName,
                        "Invalid %s '%s'. Allowed values: %s".formatted(fieldName, rawValue, allowedValues)
                )
        );
    }
}