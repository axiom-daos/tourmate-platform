package axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest;

import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import axiom.daos.tourmate.platform.tourmonitoring.application.queryservices.TourExecutionQueryService;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.support.AuthenticatedActorResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the authenticated tourist's tour execution progress view.
 */
@RestController
@RequestMapping(value = "/api/v1/tourists", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Tourist Monitoring", description = "Tourist own tour progress endpoints")
public class TouristMonitoringController {

    private final TourExecutionQueryService queryService;
    private final AuthenticatedActorResolver actorResolver;

    public TouristMonitoringController(
            TourExecutionQueryService queryService,
            AuthenticatedActorResolver actorResolver
    ) {
        this.queryService = queryService;
        this.actorResolver = actorResolver;
    }

    @GetMapping("/me/tour-progress")
    @Operation(
            summary = "Get tour progress for authenticated tourist",
            description = "Retrieves the progress of the tour execution in which the authenticated tourist is enrolled."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tour progress retrieved"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Tourist role required"),
            @ApiResponse(responseCode = "404", description = "No tour execution found for this tourist")
    })
    public ResponseEntity<?> getMyTourProgress(HttpServletRequest request) {
        var actor = actorResolver.resolve(request);
        var result = queryService.getTourProgressForTourist(actor.userId(), actor.roles());
        return ResponseEntityAssembler.toResponseEntityFromResult(result, view -> view, HttpStatus.OK);
    }
}
