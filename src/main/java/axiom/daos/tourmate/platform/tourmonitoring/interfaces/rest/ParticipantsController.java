package axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest;


import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.shared.interfaces.rest.resources.MessageResource;
import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import axiom.daos.tourmate.platform.tourmonitoring.application.commandservices.ParticipantCommandService;
import axiom.daos.tourmate.platform.tourmonitoring.application.queryservices.ParticipantQueryService;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.Participant;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.queries.GetParticipantByIdQuery;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.queries.GetAllParticipantsQuery;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources.ParticipantResource;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources.CreateParticipantResource;

import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.transform.ParticipantResourceFromEntityAssembler;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.transform.CreateParticipantCommandFromResourceAssembler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller that exposes participant resources and participant administration endpoints.
 */
@RestController
@RequestMapping(value = {"/api/v1/participants",}, produces = APPLICATION_JSON_VALUE)
@Tag(name = "Participants", description = "Participant management endpoints")
public class ParticipantsController {
    private final ParticipantCommandService participantCommandService;
    private final ParticipantQueryService participantQueryService;


    /**
     * Constructor
     *
     * @param participantCommandService The {@link ParticipantCommandService} instance
     * @param participantQueryService   The {@link ParticipantQueryService} instance
     */
    public ParticipantsController(ParticipantCommandService participantCommandService, ParticipantQueryService participantQueryService) {
        this.participantCommandService = participantCommandService;
        this.participantQueryService = participantQueryService;
    }

    /**
     * Create a new participant
     *
     * @param resource The {@link CreateParticipantResource} instance
     * @return The {@link ParticipantResource} resource for the created participant
     */
    @PostMapping
    @Operation(summary = "Create a new participant", description = "Creates a new participant with title and description. Requires instructor or admin role.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Participant created successfully",
                    content = @Content(schema = @Schema(implementation = ParticipantResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    public ResponseEntity<?> createParticipant(@RequestBody CreateParticipantResource resource) {
        var createParticipantCommand = CreateParticipantCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = participantCommandService.handle(createParticipantCommand)
                .flatMap(participantId -> participantQueryService.handle(new GetParticipantByIdQuery(participantId))
                        .<Result<Participant, ApplicationError>>
                                map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("Participant", participantId.toString()))));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                ParticipantResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    /**
     * Get participant by id
     *
     * @param participantId The participant id
     * @return The {@link ParticipantResource} resource for the participant
     */
    @GetMapping("/{participantId}")
    @Operation(summary = "Get participant by ID", description = "Retrieves a specific participant by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Participant found",
                    content = @Content(schema = @Schema(implementation = ParticipantResource.class))
            ),
            @ApiResponse(responseCode = "404", description = "Participant not found")
    })
    public ResponseEntity<ParticipantResource> getParticipantById(
            @PathVariable
            @Parameter(description = "Unique participant identifier", example = "1", required = true)
            Long participantId
    ) {
        var getParticipantByIdQuery = new GetParticipantByIdQuery(participantId);
        var participant = participantQueryService.handle(getParticipantByIdQuery);
        if (participant.isEmpty()) return ResponseEntity.notFound().build();
        var participantEntity = participant.get();
        var participantResource = ParticipantResourceFromEntityAssembler.toResourceFromEntity(participantEntity);
        return ResponseEntity.ok(participantResource);
    }

    /**
     * Get all participants
     *
     * @return The list of {@link ParticipantResource} resources for all participants
     */
    @GetMapping
    @Operation(summary = "Get all participants", description = "Retrieves a list of all available participants.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Participants retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ParticipantResource.class))
            )
    })
    public ResponseEntity<List<ParticipantResource>> getAllParticipants() {
        var participants = participantQueryService.handle(new GetAllParticipantsQuery());
        var participantResources = participants.stream()
                .map(ParticipantResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(participantResources);
    }

}
