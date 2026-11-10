package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * REST request resource to subscribe an agency to a plan.
 */
@Schema(name = "CreateSubscriptionRequest", description = "Payload to subscribe an agency to a plan")
public record CreateSubscriptionResource(
        @Schema(description = "Agency identifier", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
        Long agencyId,

        @Schema(description = "Subscribed plan identifier", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        Long planId
) {
}
