package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.commandservices.PlanCommandService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.queryservices.PlanQueryService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Plan;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.DeletePlanCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetAllPlansQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetPlanByIdQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources.CreatePlanResource;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources.PlanResource;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources.UpdatePlanResource;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.transform.CreatePlanCommandFromResourceAssembler;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.transform.PlanResourceFromEntityAssembler;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.transform.UpdatePlanCommandFromResourceAssembler;
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
 * REST controller for commercial subscription plan catalog management.
 */
@RestController
@RequestMapping(value = "/api/v1/plans", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Plans", description = "TourMate subscription plans commercial catalog endpoints")
public class PlansController {

    private final PlanCommandService planCommandService;
    private final PlanQueryService planQueryService;

    public PlansController(PlanCommandService planCommandService, PlanQueryService planQueryService) {
        this.planCommandService = planCommandService;
        this.planQueryService = planQueryService;
    }

    /**
     * Create a new subscription plan in the commercial catalog.
     */
    @PostMapping
    @Operation(summary = "Create a subscription plan", description = "Creates a new commercial subscription plan tier with pricing and quotas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plan created successfully",
                    content = @Content(schema = @Schema(implementation = PlanResource.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Plan name conflict")
    })
    public ResponseEntity<?> createPlan(@RequestBody CreatePlanResource resource) {
        var command = CreatePlanCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = planCommandService.handle(command)
                .flatMap(planId -> planQueryService.handle(new GetPlanByIdQuery(planId))
                        .<Result<Plan, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("Plan", planId.toString()))));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PlanResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    /**
     * Retrieve all available subscription plans.
     */
    @GetMapping
    @Operation(summary = "Get all subscription plans", description = "Returns the list of all active commercial subscription plans.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plans retrieved successfully")
    })
    public ResponseEntity<List<PlanResource>> getAllPlans() {
        var plans = planQueryService.handle(new GetAllPlansQuery());
        var resources = plans.stream()
                .map(PlanResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    /**
     * Retrieve a specific plan by identifier.
     */
    @GetMapping("/{planId}")
    @Operation(summary = "Get plan by id", description = "Returns details for a specific subscription plan tier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan found",
                    content = @Content(schema = @Schema(implementation = PlanResource.class))),
            @ApiResponse(responseCode = "404", description = "Plan not found")
    })
    public ResponseEntity<PlanResource> getPlanById(@Parameter(description = "Plan identifier", required = true) @PathVariable Long planId) {
        var plan = planQueryService.handle(new GetPlanByIdQuery(planId));
        return plan.map(p -> ResponseEntity.ok(PlanResourceFromEntityAssembler.toResourceFromEntity(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Update an existing subscription plan.
     */
    @PutMapping("/{planId}")
    @Operation(summary = "Update subscription plan", description = "Modifies pricing, description, or tour quotas for a plan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan updated successfully",
                    content = @Content(schema = @Schema(implementation = PlanResource.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Plan not found"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    public ResponseEntity<?> updatePlan(
            @Parameter(description = "Plan identifier", required = true) @PathVariable Long planId,
            @RequestBody UpdatePlanResource resource
    ) {
        var command = UpdatePlanCommandFromResourceAssembler.toCommandFromResource(planId, resource);
        var result = planCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PlanResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    /**
     * Delete a subscription plan.
     */
    @DeleteMapping("/{planId}")
    @Operation(summary = "Delete subscription plan", description = "Removes a subscription plan from the commercial catalog.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Plan deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Plan not found")
    })
    public ResponseEntity<?> deletePlan(@Parameter(description = "Plan identifier", required = true) @PathVariable Long planId) {
        var command = new DeletePlanCommand(planId);
        var result = planCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                id -> ResponseEntity.noContent().build(),
                HttpStatus.NO_CONTENT
        );
    }
}
