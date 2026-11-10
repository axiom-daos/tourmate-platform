package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.commandservices.SubscriptionCommandService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.queryservices.SubscriptionQueryService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Subscription;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CancelSubscriptionCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetAllSubscriptionsQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetSubscriptionByIdQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetSubscriptionsByAgencyIdQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources.CreateSubscriptionResource;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources.SubscriptionResource;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.transform.CreateSubscriptionCommandFromResourceAssembler;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
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
 * REST controller for managing agency subscriptions and lifecycle operations.
 */
@RestController
@RequestMapping(value = "/api/v1/subscriptions", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Subscriptions", description = "Agency subscription lifecycle endpoints")
public class SubscriptionsController {

    private final SubscriptionCommandService subscriptionCommandService;
    private final SubscriptionQueryService subscriptionQueryService;

    public SubscriptionsController(
            SubscriptionCommandService subscriptionCommandService,
            SubscriptionQueryService subscriptionQueryService
    ) {
        this.subscriptionCommandService = subscriptionCommandService;
        this.subscriptionQueryService = subscriptionQueryService;
    }

    /**
     * Subscribe an agency to a commercial plan.
     */
    @PostMapping
    @Operation(summary = "Subscribe agency to a plan", description = "Activates a subscription tier for an agency.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription created successfully",
                    content = @Content(schema = @Schema(implementation = SubscriptionResource.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Plan not found")
    })
    public ResponseEntity<?> createSubscription(@RequestBody CreateSubscriptionResource resource) {
        var command = CreateSubscriptionCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = subscriptionCommandService.handle(command)
                .flatMap(subscriptionId -> subscriptionQueryService.handle(new GetSubscriptionByIdQuery(subscriptionId))
                        .<Result<Subscription, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("Subscription", subscriptionId.toString()))));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                SubscriptionResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    /**
     * Retrieve all subscriptions, optionally filtered by agencyId.
     */
    @GetMapping
    @Operation(summary = "Get subscriptions", description = "Retrieves all subscriptions, or filters by agency id if provided.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully")
    })
    public ResponseEntity<List<SubscriptionResource>> getSubscriptions(
            @Parameter(description = "Optional agency identifier to filter subscriptions")
            @RequestParam(required = false) Long agencyId
    ) {
        List<Subscription> subscriptions;
        if (agencyId != null) {
            subscriptions = subscriptionQueryService.handle(new GetSubscriptionsByAgencyIdQuery(agencyId));
        } else {
            subscriptions = subscriptionQueryService.handle(new GetAllSubscriptionsQuery());
        }

        var resources = subscriptions.stream()
                .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    /**
     * Retrieve subscription details by identifier.
     */
    @GetMapping("/{subscriptionId}")
    @Operation(summary = "Get subscription by id", description = "Returns details for a specific agency subscription contract.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription found",
                    content = @Content(schema = @Schema(implementation = SubscriptionResource.class))),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    public ResponseEntity<SubscriptionResource> getSubscriptionById(
            @Parameter(description = "Subscription identifier", required = true) @PathVariable Long subscriptionId
    ) {
        var subscription = subscriptionQueryService.handle(new GetSubscriptionByIdQuery(subscriptionId));
        return subscription.map(s -> ResponseEntity.ok(SubscriptionResourceFromEntityAssembler.toResourceFromEntity(s)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Cancel an active subscription.
     */
    @PostMapping("/{subscriptionId}/cancel")
    @Operation(summary = "Cancel subscription", description = "Cancels an existing subscription contract.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription cancelled successfully",
                    content = @Content(schema = @Schema(implementation = SubscriptionResource.class))),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    public ResponseEntity<?> cancelSubscription(
            @Parameter(description = "Subscription identifier", required = true) @PathVariable Long subscriptionId,
            @Parameter(description = "Reason for cancellation") @RequestParam(required = false, defaultValue = "Unspecified") String reason
    ) {
        var command = new CancelSubscriptionCommand(subscriptionId, reason);
        var result = subscriptionCommandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                SubscriptionResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }
}
