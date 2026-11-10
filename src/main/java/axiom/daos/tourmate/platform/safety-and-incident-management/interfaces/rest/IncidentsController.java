package axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest;

import axiom.daos.tourmate.platform.safety.application.internal.commandservices.IncidentCommandService;
import axiom.daos.tourmate.platform.safety.application.internal.queryservices.IncidentQueryService;
import axiom.daos.tourmate.platform.safety.domain.model.valueobjects.IncidentSeverity;
import axiom.daos.tourmate.platform.safety.domain.model.valueobjects.IncidentStatus;
import axiom.daos.tourmate.platform.safety.interfaces.rest.resources.IncidentResource;
import axiom.daos.tourmate.platform.safety.interfaces.rest.transform.IncidentResourceAssembler;
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
public class IncidentsController {

    private final IncidentCommandService incidentCommandService;
    private final IncidentQueryService incidentQueryService;

    public IncidentsController(IncidentCommandService incidentCommandService, IncidentQueryService incidentQueryService) {
        this.incidentCommandService = incidentCommandService;
        this.incidentQueryService = incidentQueryService;
    }

    @GetMapping
    public ResponseEntity<List<IncidentResource>> getAllIncidents() {
        var incidents = incidentQueryService.getAllIncidents();
        var resources = incidents.stream()
                .map(IncidentResourceAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResource> getIncidentById(@PathVariable Long id) {
        return incidentQueryService.getIncidentById(id)
                .map(IncidentResourceAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<IncidentResource> createIncident(@RequestBody IncidentResource resource) {
        var createdIncident = incidentCommandService.createIncident(
                resource.uuid(),
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
    public ResponseEntity<IncidentResource> updateIncident(@PathVariable Long id, @RequestBody IncidentResource resource) {
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
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        boolean deleted = incidentCommandService.deleteIncident(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}