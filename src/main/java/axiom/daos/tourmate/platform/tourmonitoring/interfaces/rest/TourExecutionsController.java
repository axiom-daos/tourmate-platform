package axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest;

import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import axiom.daos.tourmate.platform.tourmonitoring.application.commandservices.TourExecutionCommandService;
import axiom.daos.tourmate.platform.tourmonitoring.application.queryservices.TourExecutionQueryService;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.CreateTourExecutionCommand;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.RiskLevel;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourStatus;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources.TourExecutionResources;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.support.AuthenticatedActorResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/**
 * Thin REST controller exposing {@code TourExecution} commands and read-only projections.
 */
@RestController
@RequestMapping(value = "/api/v1/tour-executions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Tour Executions", description = "Real-time tour execution monitoring commands and queries")
public class TourExecutionsController {

    private final TourExecutionCommandService commandService;
    private final TourExecutionQueryService queryService;
    private final AuthenticatedActorResolver actorResolver;

    public TourExecutionsController(
            TourExecutionCommandService commandService,
            TourExecutionQueryService queryService,
            AuthenticatedActorResolver actorResolver
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.actorResolver = actorResolver;
    }

    @PostMapping
    @Operation(
            summary = "Create a scheduled tour execution",
            description = "Provisions a new tour execution in SCHEDULED status with route checkpoints and participants."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tour execution created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<?> createTourExecution(
            @Valid @RequestBody TourExecutionResources.CreateTourExecutionResource resource,
            HttpServletRequest request
    ) {
        var actor = actorResolver.resolve(request);
        List<CreateTourExecutionCommand.CheckpointSeed> checkpoints = resource.checkpoints() == null
                ? List.of()
                : resource.checkpoints().stream()
                .map(cp -> new CreateTourExecutionCommand.CheckpointSeed(
                        cp.id(),
                        cp.name(),
                        cp.sequenceOrder(),
                        cp.latitude(),
                        cp.longitude(),
                        cp.expectedArrival(),
                        cp.latestAllowedArrival()
                ))
                .toList();

        List<CreateTourExecutionCommand.ParticipantSeed> participants = resource.participants() == null
                ? List.of()
                : resource.participants().stream()
                .map(p -> new CreateTourExecutionCommand.ParticipantSeed(
                        p.participantId(),
                        p.touristId(),
                        p.bookingId()
                ))
                .toList();

        var command = new CreateTourExecutionCommand(
                resource.tourId(),
                resource.guideId(),
                resource.bookingId(),
                resource.scheduledStartAt(),
                resource.initialLatitude(),
                resource.initialLongitude(),
                checkpoints,
                participants,
                actor.userId(),
                actor.roles()
        );
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourExecutionResources.TourExecutionStateResource::fromDomain,
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{id}/participants/{participantId}/check-in")
    @Operation(
            summary = "Check in a participant",
            description = "Records participant attendance before or during the tour execution. Restricted to the assigned guide."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Participant checked in"),
            @ApiResponse(responseCode = "403", description = "Not the assigned guide"),
            @ApiResponse(responseCode = "404", description = "Execution or participant not found"),
            @ApiResponse(responseCode = "409", description = "Invalid execution state")
    })
    public ResponseEntity<?> checkInParticipant(
            @PathVariable Long id,
            @PathVariable Long participantId,
            HttpServletRequest request
    ) {
        var actor = actorResolver.resolve(request);
        var command = new CreateTourExecutionCommand.CheckInParticipantCommand(
                id,
                participantId,
                actor.userId(),
                actor.roles()
        );
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourExecutionResources.ParticipantCheckInResource::fromDomain,
                HttpStatus.OK
        );
    }

    @PostMapping("/{id}/start")
    @Operation(
            summary = "Start tour execution",
            description = "Transitions from SCHEDULED to IN_PROGRESS. Requires assigned guide and minimum check-ins."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tour execution started"),
            @ApiResponse(responseCode = "403", description = "Not the assigned guide"),
            @ApiResponse(responseCode = "404", description = "Tour execution not found"),
            @ApiResponse(responseCode = "409", description = "Invalid state transition"),
            @ApiResponse(responseCode = "422", description = "Business rule violated (insufficient check-ins or disabled guide)")
    })
    public ResponseEntity<?> startTourExecution(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        var actor = actorResolver.resolve(request);
        var command = new CreateTourExecutionCommand.StartTourExecutionCommand(
                id,
                actor.userId(),
                actor.roles()
        );
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourExecutionResources.TourExecutionStateResource::fromDomain,
                HttpStatus.OK
        );
    }

    @PostMapping("/{id}/checkpoints/{checkpointId}/reach")
    @Operation(
            summary = "Mark checkpoint reached",
            description = "Marks a checkpoint reached while IN_PROGRESS, computing delay against TimeWindow."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Checkpoint reached"),
            @ApiResponse(responseCode = "403", description = "Not the assigned guide"),
            @ApiResponse(responseCode = "404", description = "Execution or checkpoint not found"),
            @ApiResponse(responseCode = "409", description = "Not IN_PROGRESS or checkpoint already reached")
    })
    public ResponseEntity<?> reachCheckpoint(
            @PathVariable Long id,
            @PathVariable Long checkpointId,
            HttpServletRequest request
    ) {
        var actor = actorResolver.resolve(request);
        var command = new CreateTourExecutionCommand.ReachCheckpointCommand(
                id,
                checkpointId,
                actor.userId(),
                actor.roles()
        );
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourExecutionResources.CheckpointReachedResource::fromDomain,
                HttpStatus.OK
        );
    }

    @PostMapping("/{id}/incidents")
    @Operation(
            summary = "Report an incident during a tour execution",
            description = "Records an incident (GUIDE or enrolled TOURIST) and automatically evaluates RiskLevel escalation."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Incident reported"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Invalid state")
    })
    public ResponseEntity<?> reportIncident(
            @PathVariable Long id,
            @Valid @RequestBody TourExecutionResources.ReportIncidentResource resource,
            HttpServletRequest request
    ) {
        var actor = actorResolver.resolve(request);
        var command = new CreateTourExecutionCommand.ReportIncidentCommand(
                id,
                resource.type(),
                resource.severity(),
                resource.description(),
                resource.latitude(),
                resource.longitude(),
                actor.userId(),
                actor.roles()
        );
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourExecutionResources.ExecutionIncidentResource::fromDomain,
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{id}/incidents/{incidentId}/resolve")
    @Operation(
            summary = "Resolve an incident",
            description = "Resolves an open incident with mandatory resolution notes (GUIDE or ADMIN)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incident resolved"),
            @ApiResponse(responseCode = "400", description = "Missing resolution notes"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Incident not found"),
            @ApiResponse(responseCode = "409", description = "Incident already resolved")
    })
    public ResponseEntity<?> resolveIncident(
            @PathVariable Long id,
            @PathVariable Long incidentId,
            @Valid @RequestBody TourExecutionResources.ResolveIncidentResource resource,
            HttpServletRequest request
    ) {
        var actor = actorResolver.resolve(request);
        var command = new CreateTourExecutionCommand.ResolveIncidentCommand(
                id,
                incidentId,
                resource.resolutionNotes(),
                actor.userId(),
                actor.roles()
        );
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourExecutionResources.ExecutionIncidentResource::fromDomain,
                HttpStatus.OK
        );
    }

    @PostMapping("/{id}/sos")
    @Operation(
            summary = "Trigger emergency SOS alert",
            description = "Creates a critical SOS alert for administrators. Idempotent if an active SOS from the same user already exists."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "SOS alert triggered"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Invalid execution state")
    })
    public ResponseEntity<?> triggerSos(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) TourExecutionResources.TriggerSosResource resource,
            HttpServletRequest request
    ) {
        var actor = actorResolver.resolve(request);
        var command = new CreateTourExecutionCommand.TriggerSosCommand(
                id,
                resource != null ? resource.reason() : null,
                resource != null ? resource.latitude() : null,
                resource != null ? resource.longitude() : null,
                actor.userId(),
                actor.roles()
        );
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourExecutionResources.ExecutionAlertResource::fromDomain,
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{id}/pause")
    @Operation(
            summary = "Pause tour execution",
            description = "Pauses an in-progress tour execution with a mandatory reason (GUIDE or ADMIN)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tour execution paused"),
            @ApiResponse(responseCode = "400", description = "Reason is required"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Invalid state transition")
    })
    public ResponseEntity<?> pauseTourExecution(
            @PathVariable Long id,
            @Valid @RequestBody TourExecutionResources.ReasonResource resource,
            HttpServletRequest request
    ) {
        var actor = actorResolver.resolve(request);
        var command = new CreateTourExecutionCommand.PauseTourExecutionCommand(
                id,
                resource.reason(),
                actor.userId(),
                actor.roles()
        );
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourExecutionResources.TourExecutionStateResource::fromDomain,
                HttpStatus.OK
        );
    }

    @PostMapping("/{id}/resume")
    @Operation(
            summary = "Resume paused tour execution",
            description = "Resumes a PAUSED tour execution back to IN_PROGRESS (GUIDE or ADMIN)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tour execution resumed"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Invalid state transition")
    })
    public ResponseEntity<?> resumeTourExecution(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        var actor = actorResolver.resolve(request);
        var command = new CreateTourExecutionCommand.ResumeTourExecutionCommand(
                id,
                actor.userId(),
                actor.roles()
        );
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourExecutionResources.TourExecutionStateResource::fromDomain,
                HttpStatus.OK
        );
    }

    @PostMapping("/{id}/complete")
    @Operation(
            summary = "Complete tour execution",
            description = "Completes an IN_PROGRESS tour execution if no unresolved critical incidents remain."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tour execution completed"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Invalid state transition"),
            @ApiResponse(responseCode = "422", description = "Unresolved critical incidents exist")
    })
    public ResponseEntity<?> completeTourExecution(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        var actor = actorResolver.resolve(request);
        var command = new CreateTourExecutionCommand.CompleteTourExecutionCommand(
                id,
                actor.userId(),
                actor.roles()
        );
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourExecutionResources.TourExecutionStateResource::fromDomain,
                HttpStatus.OK
        );
    }

    @PostMapping("/{id}/abort")
    @Operation(
            summary = "Abort tour execution",
            description = "Transitions tour execution to terminal ABORTED status with mandatory reason (ADMIN or GUIDE)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tour execution aborted"),
            @ApiResponse(responseCode = "400", description = "Reason is required"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Already in terminal status")
    })
    public ResponseEntity<?> abortTourExecution(
            @PathVariable Long id,
            @Valid @RequestBody TourExecutionResources.ReasonResource resource,
            HttpServletRequest request
    ) {
        var actor = actorResolver.resolve(request);
        var command = new CreateTourExecutionCommand.AbortTourExecutionCommand(
                id,
                resource.reason(),
                actor.userId(),
                actor.roles()
        );
        var result = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourExecutionResources.TourExecutionStateResource::fromDomain,
                HttpStatus.OK
        );
    }

    @GetMapping("/active")
    @Operation(
            summary = "Get active tour executions (Admin)",
            description = "Paginated query of active tour executions with optional filters for status, riskLevel, guideId, and date range."
    )
    public ResponseEntity<?> getActiveExecutions(
            @Parameter(description = "Filter by status") @RequestParam(required = false) TourStatus status,
            @Parameter(description = "Filter by risk level") @RequestParam(required = false) RiskLevel riskLevel,
            @Parameter(description = "Filter by guide ID") @RequestParam(required = false) Long guideId,
            @Parameter(description = "Start of scheduled date range (ISO-8601)") @RequestParam(required = false) Instant fromDate,
            @Parameter(description = "End of scheduled date range (ISO-8601)") @RequestParam(required = false) Instant toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        var actor = actorResolver.resolve(request);
        var pageable = PageRequest.of(Math.max(0, page), Math.max(1, Math.min(100, size)));
        var result = queryService.findActiveExecutions(
                status,
                riskLevel,
                guideId,
                fromDate,
                toDate,
                pageable,
                actor.roles()
        );
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                p -> new TourExecutionResources.PagedActiveExecutionsResource(
                        p.getContent(),
                        p.getNumber(),
                        p.getSize(),
                        p.getTotalElements(),
                        p.getTotalPages()
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}/live")
    @Operation(
            summary = "Get live state of a tour execution",
            description = "Returns status, risk level, next checkpoint, current delay, checkpoints, incidents, and active alerts."
    )
    public ResponseEntity<?> getLiveState(@PathVariable Long id, HttpServletRequest request) {
        var actor = actorResolver.resolve(request);
        var result = queryService.getLiveState(id, actor.userId(), actor.roles());
        return ResponseEntityAssembler.toResponseEntityFromResult(result, view -> view, HttpStatus.OK);
    }

    @GetMapping("/{id}/timeline")
    @Operation(
            summary = "Get chronological timeline of a tour execution",
            description = "Returns all events recorded on the tour execution ordered chronologically."
    )
    public ResponseEntity<?> getTimeline(@PathVariable Long id, HttpServletRequest request) {
        var actor = actorResolver.resolve(request);
        var result = queryService.getTimeline(id, actor.userId(), actor.roles());
        return ResponseEntityAssembler.toResponseEntityFromResult(result, list -> list, HttpStatus.OK);
    }

    @GetMapping("/{id}/participants/status")
    @Operation(
            summary = "Get participants attendance status",
            description = "Returns present, absent, and pending counts and individual participant check-in states."
    )
    public ResponseEntity<?> getParticipantsStatus(@PathVariable Long id, HttpServletRequest request) {
        var actor = actorResolver.resolve(request);
        var result = queryService.getParticipantsStatus(id, actor.userId(), actor.roles());
        return ResponseEntityAssembler.toResponseEntityFromResult(result, view -> view, HttpStatus.OK);
    }

    @GetMapping("/{id}/summary")
    @Operation(
            summary = "Get post-tour execution summary",
            description = "Returns duration, reached checkpoints, total delays, and incident resolution breakdown."
    )
    public ResponseEntity<?> getSummary(@PathVariable Long id, HttpServletRequest request) {
        var actor = actorResolver.resolve(request);
        var result = queryService.getSummary(id, actor.userId(), actor.roles());
        return ResponseEntityAssembler.toResponseEntityFromResult(result, view -> view, HttpStatus.OK);
    }
}
