package axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest;

import axiom.daos.tourmate.platform.safety_and_incident_management.application.internal.commandservices.IncidentCommandService;
import axiom.daos.tourmate.platform.safety_and_incident_management.application.internal.queryservices.IncidentQueryService;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.valueobjects.IncidentSeverity;
import axiom.daos.tourmate.platform.safety_and_incident_management.domain.model.valueobjects.IncidentStatus;
import axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest.resources.CreateIncidentResource;
import axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest.resources.IncidentResource;
import axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest.resources.UpdateIncidentResource;
import axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest.transform.IncidentResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@NullMarked
@RestController
@RequestMapping("/api/v1/incidents")
@Tag(name = "Incidents", description = "Available Incident Management CRUD Endpoints")
/**
 * Controller class for managing incidents, providing RESTful endpoints for creating, retrieving, updating, and deleting incidents.
 */
public class IncidentsController {

    private final IncidentCommandService incidentCommandService;
    private final IncidentQueryService incidentQueryService;

    public IncidentsController(IncidentCommandService incidentCommandService, IncidentQueryService incidentQueryService) {
        this.incidentCommandService = incidentCommandService;
        this.incidentQueryService = incidentQueryService;
    }

    @GetMapping
    /**
     * Retrieves all incidents and returns them as a list of IncidentResource representations.
     */
    public ResponseEntity<List<IncidentResource>> getAllIncidents() {
        var incidents = incidentQueryService.getAllIncidents();
        var resources = incidents.stream()
                .map(IncidentResourceAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    /**
     * Retrieves an incident by its ID and returns it as an IncidentResource. If the incident is not found, returns a 404 Not Found response.
     */
    public ResponseEntity<IncidentResource> getIncidentById(@PathVariable Long id) {
        return incidentQueryService.getIncidentById(id)
                .map(IncidentResourceAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    /**
     * Creates a new incident based on the provided CreateIncidentResource payload.
     * @param resource
     * @return
     */
    public ResponseEntity<IncidentResource> createIncident(@RequestBody CreateIncidentResource resource) {
        var createdIncident = incidentCommandService.createIncident(
                resource.activeTourId(),
                resource.reportedByUserId(),
                resource.description(),
                resource.latitude(),
                resource.longitude(),
                resource.reportedAt(),
                resource.status() != null ? IncidentStatus.valueOf(resource.status()) : null,
                resource.severity() != null ? IncidentSeverity.valueOf(resource.severity()) : null,
                resource.resolutionNote(),
                resource.reporterRole()
        );
        var createdResource = IncidentResourceAssembler.toResourceFromEntity(createdIncident);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdResource);
    }

    @PutMapping("/{id}")
    /**
     * Updates an existing incident.
     * @param id
     * @param resource
     * @return
     */
    public ResponseEntity<IncidentResource> updateIncident(@PathVariable Long id, @RequestBody UpdateIncidentResource resource) {
        var updatedIncidentOpt = incidentCommandService.updateIncident(
                id,
                resource.activeTourId(),
                resource.reportedByUserId(),
                resource.description(),
                resource.latitude(),
                resource.longitude(),
                resource.reportedAt(),
                resource.status() != null ? IncidentStatus.valueOf(resource.status()) : IncidentStatus.OPEN,
                resource.severity() != null ? IncidentSeverity.valueOf(resource.severity()) : IncidentSeverity.MEDIUM,
                resource.resolutionNote(),
                resource.reporterRole() != null ? resource.reporterRole() : "TOURIST"
        );

        return updatedIncidentOpt
                .map(IncidentResourceAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
/**
     * Deletes an existing incident by its ID.
     * @param id
     * @return
     */
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        boolean deleted = incidentCommandService.deleteIncident(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}