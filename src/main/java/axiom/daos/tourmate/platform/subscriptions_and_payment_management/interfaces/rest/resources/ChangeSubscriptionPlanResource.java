package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * REST request resource to change the commercial plan of an active subscription.
 *
 * @param newPlanId Target commercial plan identifier
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "ChangeSubscriptionPlanRequest", description = "Payload to upgrade or downgrade an active agency subscription")
public record ChangeSubscriptionPlanResource(
        @Schema(description = "Target commercial plan identifier", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long newPlanId
) {
}
