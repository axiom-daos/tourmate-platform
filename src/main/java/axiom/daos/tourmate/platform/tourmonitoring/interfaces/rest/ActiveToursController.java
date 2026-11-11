package axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.shared.interfaces.rest.resources.MessageResource;
import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import axiom.daos.tourmate.platform.tourmonitoring.application.commandservices.ActiveTourCommandService;
import axiom.daos.tourmate.platform.tourmonitoring.application.queryservices.ActiveTourQueryService;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.ActiveTour;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.DeleteActiveTourCommand;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.queries.GetActiveTourByIdQuery;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.queries.GetAllActiveToursQuery;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources.ActiveTourResource;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources.CreateActiveTourResource;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources.UpdateActiveTourResource;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.transform.ActiveTourResourceFromEntityAssembler;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.transform.CreateActiveTourCommandFromResourceAssembler;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.transform.UpdateActiveTourCommandFromResourceAssembler;
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
 * REST controller that exposes active tour resources and active tour administration endpoints.
 */
@RestController
@RequestMapping(value = {"/api/v1/active-tours", "/api/v1/active_tours"}, produces = APPLICATION_JSON_VALUE)
@Tag(name = "ActiveTours", description = "ActiveTour management endpoints")
public class ActiveToursController {
    private final ActiveTourCommandService activeTourCommandService;
    private final ActiveTourQueryService activeTourQueryService;


    /**
     * Constructor
     *
     * @param activeTourCommandService The {@link ActiveTourCommandService} instance
     * @param activeTourQueryService   The {@link ActiveTourQueryService} instance
     */
    public ActiveToursController(ActiveTourCommandService activeTourCommandService, ActiveTourQueryService activeTourQueryService) {
        this.activeTourCommandService = activeTourCommandService;
        this.activeTourQueryService = activeTourQueryService;
    }

    /**
     * Create a new activeTour
     *
     * @param resource The {@link CreateActiveTourResource} instance
     * @return The {@link ActiveTourResource} resource for the created activeTour
     */
    @PostMapping
    @Operation(summary = "Create a new active tour", description = "Creates a new active tour with title and description. Requires instructor or admin role.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "ActiveTour created successfully",
                    content = @Content(schema = @Schema(implementation = ActiveTourResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    public ResponseEntity<?> createActiveTour(@RequestBody CreateActiveTourResource resource) {
        var createActiveTourCommand = CreateActiveTourCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = activeTourCommandService.handle(createActiveTourCommand)
                .flatMap(activeTourId -> activeTourQueryService.handle(new GetActiveTourByIdQuery(activeTourId))
                        .<Result<ActiveTour, ApplicationError>>
                                map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("ActiveTour", activeTourId.toString()))));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                ActiveTourResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    /**
     * Get active tour by id
     *
     * @param activeTourId The active tour id
     * @return The {@link ActiveTourResource} resource for the active tourr
     */
    @GetMapping("/{activeTourId}")
    @Operation(summary = "Get active tour by ID", description = "Retrieves a specific active tour by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ActiveTour found",
                    content = @Content(schema = @Schema(implementation = ActiveTourResource.class))
            ),
            @ApiResponse(responseCode = "404", description = "ActiveTour not found")
    })
    public ResponseEntity<ActiveTourResource> getActiveTourById(
            @PathVariable
            @Parameter(description = "Unique active tour identifier", example = "1", required = true)
            Long activeTourId
    ) {
        var getActiveTourByIdQuery = new GetActiveTourByIdQuery(activeTourId);
        var activeTour = activeTourQueryService.handle(getActiveTourByIdQuery);
        if (activeTour.isEmpty()) return ResponseEntity.notFound().build();
        var activeTourEntity = activeTour.get();
        var activeTourResource = ActiveTourResourceFromEntityAssembler.toResourceFromEntity(activeTourEntity);
        return ResponseEntity.ok(activeTourResource);
    }

    /**
     * Get all active tours
     *
     * @return The list of {@link ActiveTourResource} resources for all active tours
     */
    @GetMapping
    @Operation(summary = "Get all active tours", description = "Retrieves a list of all available activ tours.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ActiveTours retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ActiveTourResource.class))
            )
    })
    public ResponseEntity<List<ActiveTourResource>> getAllActiveTours() {
        var activeTours = activeTourQueryService.handle(new GetAllActiveToursQuery());
        var activeTourResources = activeTours.stream()
                .map(ActiveTourResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(activeTourResources);
    }

    /**
     * Update active tour
     *
     * @param activeTourId The active tour id
     * @param resource The {@link UpdateActiveTourResource} instance
     * @return The {@link ActiveTourResource} resource for the updated active tour
     */
    @PutMapping("/{activeTourId}")
    @Operation(summary = "Update active tour", description = "Updates an existing active tour's title and description. Requires instructor or admin role.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ActiveTour updated successfully",
                    content = @Content(schema = @Schema(implementation = ActiveTourResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "ActiveTour not found")
    })
    public ResponseEntity<?> updateActiveTour(
            @PathVariable
            @Parameter(description = "Unique active tour identifier", example = "1", required = true)
            Long activeTourId,
            @RequestBody UpdateActiveTourResource resource
    ) {
        var updateActiveTourCommand = UpdateActiveTourCommandFromResourceAssembler.toCommandFromResource(activeTourId, resource);
        var result = activeTourCommandService.handle(updateActiveTourCommand);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                ActiveTourResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    /**
     * Delete active tour
     *
     * @param activeTourId The active tour id
     * @return The message for the deleted active tour
     */
    @DeleteMapping("/{activeTourId}")
    @Operation(summary = "Delete active tour", description = "Deletes a active tour. Requires instructor or admin role.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Active tour deleted successfully"
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Active Tour not found")
    })
    public ResponseEntity<?> deleteActiveTour(
            @PathVariable
            @Parameter(description = "Unique active tour identifier", example = "1", required = true)
            Long activeTourId
    ) {
        var deleteActiveTourCommand = new DeleteActiveTourCommand(activeTourId);
        var result = activeTourCommandService.handle(deleteActiveTourCommand)
                .map(_ -> new MessageResource("ActiveTour with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                message -> message,
                HttpStatus.OK
        );
    }
}
