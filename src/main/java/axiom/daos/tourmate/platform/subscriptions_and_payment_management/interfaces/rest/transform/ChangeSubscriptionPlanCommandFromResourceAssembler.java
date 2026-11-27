package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.ChangeSubscriptionPlanCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources.ChangeSubscriptionPlanResource;

/**
 * Assembler to create {@link ChangeSubscriptionPlanCommand} from {@link ChangeSubscriptionPlanResource}.
 */
public final class ChangeSubscriptionPlanCommandFromResourceAssembler {

    private ChangeSubscriptionPlanCommandFromResourceAssembler() {
    }

    /**
     * Converts path identifier and request payload into a {@link ChangeSubscriptionPlanCommand}.
     *
     * @param subscriptionId the subscription identifier from the path
     * @param resource       the REST request payload
     * @return the resulting domain command
     */
    public static ChangeSubscriptionPlanCommand toCommandFromResource(
            Long subscriptionId,
            ChangeSubscriptionPlanResource resource
    ) {
        return new ChangeSubscriptionPlanCommand(subscriptionId, resource.newPlanId());
    }
}
