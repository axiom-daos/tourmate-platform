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
 * REST controller for the authenticated guide's active tour execution view.
 */
@RestController
@RequestMapping(value = "/api/v1/guides", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Guide Monitoring", description = "Guide current tour monitoring endpoints")
public class GuideMonitoringController {

    private final TourExecutionQueryService queryService;
    private final AuthenticatedActorResolver actorResolver;

    public GuideMonitoringController(
            TourExecutionQueryService queryService,
            AuthenticatedActorResolver actorResolver
    ) {
        this.queryService = queryService;
        this.actorResolver = actorResolver;
    }

    @GetMapping("/me/current-tour")
    @Operation(
            summary = "Get current tour for authenticated guide",
            description = "Retrieves the active (SCHEDULED, IN_PROGRESS, or PAUSED) tour execution assigned to the authenticated guide."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current tour retrieved"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Guide role required"),
            @ApiResponse(responseCode = "404", description = "No active tour execution assigned to this guide")
    })
    public ResponseEntity<?> getMyCurrentTour(HttpServletRequest request) {
        var actor = actorResolver.resolve(request);
        var result = queryService.getCurrentTourForGuide(actor.userId(), actor.roles());
        return ResponseEntityAssembler.toResponseEntityFromResult(result, view -> view, HttpStatus.OK);
    }
}
