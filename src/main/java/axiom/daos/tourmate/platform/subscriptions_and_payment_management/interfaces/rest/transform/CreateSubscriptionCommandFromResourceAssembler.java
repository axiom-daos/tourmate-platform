package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CreateSubscriptionCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources.CreateSubscriptionResource;

/**
 * Assembler to create {@link CreateSubscriptionCommand} from {@link CreateSubscriptionResource}.
 */
public final class CreateSubscriptionCommandFromResourceAssembler {

    private CreateSubscriptionCommandFromResourceAssembler() {
    }

    public static CreateSubscriptionCommand toCommandFromResource(CreateSubscriptionResource resource) {
        return new CreateSubscriptionCommand(
                resource.agencyId(),
                resource.planId()
        );
    }
}
