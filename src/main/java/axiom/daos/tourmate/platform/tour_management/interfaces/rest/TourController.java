package axiom.daos.tourmate.platform.tour_management.interfaces.rest;

import axiom.daos.tourmate.platform.shared.interfaces.rest.resources.MessageResource;
import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import axiom.daos.tourmate.platform.tour_management.application.commandservices.TourCommandService;
import axiom.daos.tourmate.platform.tour_management.application.queryservices.TourQueryService;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.AddCheckpointToTourCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.DeleteTourCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.queries.GetAllToursQuery;
import axiom.daos.tourmate.platform.tour_management.domain.model.queries.GetTourByIdQuery;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.AddCheckpointResource;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.CreateTourResource;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.TourResource;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.UpdateTourResource;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.transform.AddCheckpointToTourCommandFromResourceAssembler;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.transform.CreateTourCommandFromResourceAssembler;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.transform.TourResourceFromEntityAssembler;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.transform.UpdateTourCommandFromResourceAssembler;
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

@RestController
@RequestMapping(value = "/api/v1/tours", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Tours", description = "Tour management endpoints")
public class TourController {

    private final TourCommandService tourCommandService;
    private final TourQueryService tourQueryService;

    public TourController(TourCommandService tourCommandService, TourQueryService tourQueryService) {
        this.tourCommandService = tourCommandService;
        this.tourQueryService = tourQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a new Tour", description = "Creates a new Tour with agency ID and details")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tour created successfully",
                    content = @Content(schema = @Schema(implementation = TourResource.class))
            ),
            @ApiResponse(responseCode =  "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    public ResponseEntity<?> createTour(@RequestBody CreateTourResource resource) {

        var createTourCommand = CreateTourCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = this.tourCommandService.handle(createTourCommand);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );

    }

    @PostMapping("/{tourId}/checkpoints")
    @Operation(summary = "Add a checkpoint", description = "Add a new checkpoint to an existing tour")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Checkpoint created successfully",
                    content = @Content(schema = @Schema(implementation = TourResource.class))
            ),
            @ApiResponse(responseCode =  "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Tour not found")
    })
    public ResponseEntity<?> addCheckpoint(
            @PathVariable
            @Parameter(description = "Unique tour identifier", example = "1", required = true)
            Long tourId,
            @RequestBody
            AddCheckpointResource resource
    ) {

        var addCheckpointToTourCommand = AddCheckpointToTourCommandFromResourceAssembler.toCommandFromResource(tourId, resource);
        var result = this.tourCommandService.handle(addCheckpointToTourCommand);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }


    @GetMapping("/{tourId}")
    @Operation(summary = "Get tour by ID", description = "Retrieves a specific tour by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tour found",
                    content = @Content(schema = @Schema(implementation = TourResource.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tour not found"
            )
    })
    public ResponseEntity<TourResource> getTourById(
            @PathVariable
            @Parameter(description = "Unique tour identifier", example = "1", required = true)
            Long tourId
    ) {

        var getTourByIdQuery = new GetTourByIdQuery(tourId);
        var tour = this.tourQueryService.handle(getTourByIdQuery);

        if(tour.isEmpty()) return ResponseEntity.notFound().build();
        var tourEntity = tour.get();

        var tourResource = TourResourceFromEntityAssembler.toResourceFromEntity(tourEntity);

        return ResponseEntity.ok(tourResource);
    }

    @GetMapping
    @Operation(summary = "Get All Tours", description = "Retrieves a list of all available tours")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tours retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TourResource.class))
            )
    })
    public ResponseEntity<List<TourResource>> getAllTours() {
        var tours = this.tourQueryService.handle(new GetAllToursQuery());
        var tourResources = tours.stream().map(TourResourceFromEntityAssembler::toResourceFromEntity).toList();

        return ResponseEntity.ok(tourResources);
    }

    @PutMapping("/{tourId}")
    @Operation(summary = "Update course", description = "Update the complete details for an existing tour")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Course updated successfully",
                    content = @Content(schema = @Schema(implementation = TourResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Tour not found")
    })
    public ResponseEntity<?> updateTour(
            @PathVariable
            @Parameter(description = "Unique tour identifier", example = "1", required = true)
            Long tourId,
            @RequestBody UpdateTourResource resource
    ){
        var updateTourCommand = UpdateTourCommandFromResourceAssembler.toCommandFromResource(tourId, resource);

        var result = this.tourCommandService.handle(updateTourCommand);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{tourId}")
    @Operation(summary = "Delete tour", description = "Deletes a tour")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Tour deleted successfully"
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Tour not found")
    })
    public ResponseEntity<?> deleteTour(
            @PathVariable
            @Parameter(description = "Unique tour identifier", example = "1", required = true)
            Long tourId
    ){
        var deleteTourCommand = new DeleteTourCommand(tourId);
        var result = this.tourCommandService.handle(deleteTourCommand)
                .map(_ -> new MessageResource("Tour with given id successfully deleted"));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                message -> message,
                HttpStatus.OK
        );
    }

}
