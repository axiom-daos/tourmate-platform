package axiom.daos.tourmate.platform.tour_management.interfaces.rest;

import axiom.daos.tourmate.platform.shared.interfaces.rest.resources.MessageResource;
import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import axiom.daos.tourmate.platform.tour_management.application.commandservices.TourScheduleCommandService;
import axiom.daos.tourmate.platform.tour_management.application.queryservices.TourScheduleQueryService;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.CloseTourScheduleCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.DeleteTourScheduleCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.OpenTourScheduleCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.queries.GetAllTourSchedulesQuery;
import axiom.daos.tourmate.platform.tour_management.domain.model.queries.GetTourScheduleByIdQuery;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.CreateTourScheduleResource;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.TourResource;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.TourScheduleResource;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.transform.CreateTourScheduleCommandFromResourceAssembler;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.transform.TourScheduleResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/tour-schedules", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Tour Schedules", description = "Tour schedule management endpoints")
public class TourScheduleController {

    private final TourScheduleCommandService tourScheduleCommandService;
    private final TourScheduleQueryService tourScheduleQueryService;

    public TourScheduleController(TourScheduleCommandService tourScheduleCommandService, TourScheduleQueryService tourScheduleQueryService) {
        this.tourScheduleCommandService = tourScheduleCommandService;
        this.tourScheduleQueryService = tourScheduleQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a new tour schedule")
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tour schedule created successfully",
                    content = @Content(schema = @Schema(implementation = TourScheduleResource.class))
            ),
            @ApiResponse(responseCode =  "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    public ResponseEntity<?> createTourSchedule(@RequestBody CreateTourScheduleResource resource) {

        var createTourScheduleCommand = CreateTourScheduleCommandFromResourceAssembler.toCommandFromResource(resource);

        var result = this.tourScheduleCommandService.handle(createTourScheduleCommand);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourScheduleResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{tourScheduleId}/open")
    @Operation(summary = "Open a tour schedule")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tour schedule opened successfully",
                    content = @Content(schema = @Schema(implementation = TourScheduleResource.class))
            )
    })
    public ResponseEntity<?> openTourSchedule(
            @PathVariable
            @Parameter(description = "Unique tour schedule identifier", example = "1", required = true)
            Long tourScheduleId
            ) {
        var openTourScheduleCommand = new OpenTourScheduleCommand(tourScheduleId);

        var result = this.tourScheduleCommandService.handle(openTourScheduleCommand);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourScheduleResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    @PostMapping("/{tourScheduleId}/close")
    @Operation(summary = "Close a tour schedule")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tour schedule closed successfully",
                    content = @Content(schema = @Schema(implementation = TourScheduleResource.class))
            )
    })
    public ResponseEntity<?> closeTourSchedule(
            @PathVariable
            @Parameter(description = "Unique tour schedule identifier", example = "1", required = true)
            Long tourScheduleId
    ) {
        var closeTourScheduleCommand = new CloseTourScheduleCommand(tourScheduleId);

        var result = this.tourScheduleCommandService.handle(closeTourScheduleCommand);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                TourScheduleResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    @GetMapping("/{tourScheduleId}")
    @Operation(summary = "Get tour schedule by ID", description = "Retrieves a specific tour schedule by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tour schedule found",
                    content = @Content(schema = @Schema(implementation = TourScheduleResource.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tour schedule not found"
            )
    })
    public ResponseEntity<TourScheduleResource> getTourScheduleById(
            @PathVariable
            @Parameter(description = "Unique tour schedule identifier", example = "1", required = true)
            Long tourScheduleId
    ) {

        var getTourScheduleByIdQuery = new GetTourScheduleByIdQuery(tourScheduleId);

        var tourSchedule = this.tourScheduleQueryService.handle(getTourScheduleByIdQuery);

        if(tourSchedule.isEmpty()) return ResponseEntity.notFound().build();

        var tourScheduleEntity = tourSchedule.get();

        var tourScheduleResource = TourScheduleResourceFromEntityAssembler.toResourceFromEntity(tourScheduleEntity);
        return ResponseEntity.ok(tourScheduleResource);
    }

    @GetMapping
    @Operation(summary = "Get All Tour schedules", description = "Retrieves a list of all available tour schedules")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tour schedules retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TourScheduleResource.class))
            )
    })
    public ResponseEntity<List<TourScheduleResource>> getAllTourSchedules() {

        var tourSchedules = this.tourScheduleQueryService.handle(new GetAllTourSchedulesQuery());

        var tourScheduleResources = tourSchedules.stream().map(TourScheduleResourceFromEntityAssembler::toResourceFromEntity).toList();

        return ResponseEntity.ok(tourScheduleResources);
    }

    @DeleteMapping("/{tourScheduleId}")
    @Operation(summary = "Delete tour schedule", description = "Delete a tour schedule")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Tour schedule deleted successfully"
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Tour not found")
    })
    public ResponseEntity<?> deleteTourSchedule(
            @PathVariable
            @Parameter(description = "Unique tour schedule identifier", example = "1", required = true)
            Long tourScheduleId
    ) {
        var deleteTourScheduleCommand = new DeleteTourScheduleCommand(tourScheduleId);
        var result = this.tourScheduleCommandService.handle(deleteTourScheduleCommand)
                .map(_ -> new MessageResource("Tour with given id successfully deleted"));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                message -> message,
                HttpStatus.OK
        );
    }
}
