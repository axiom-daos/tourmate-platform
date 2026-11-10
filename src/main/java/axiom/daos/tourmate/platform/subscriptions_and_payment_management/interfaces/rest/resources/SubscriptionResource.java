package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * REST response resource for an agency subscription.
 */
@Schema(name = "SubscriptionResponse", description = "Agency subscription contract details")
public record SubscriptionResource(
        @Schema(description = "Subscription unique identifier", example = "1")
        Long id,

        @Schema(description = "Agency unique identifier", example = "42")
        Long agencyId,

        @Schema(description = "Subscribed plan identifier", example = "2")
        Long planId,

        @Schema(description = "Subscription lifecycle status", example = "ACTIVE")
        String status,

        @Schema(description = "Timestamp when subscription was activated")
        Date activatedAt,

        @Schema(description = "Timestamp when subscription was cancelled, if applicable")
        Date cancelledAt
) {
}
