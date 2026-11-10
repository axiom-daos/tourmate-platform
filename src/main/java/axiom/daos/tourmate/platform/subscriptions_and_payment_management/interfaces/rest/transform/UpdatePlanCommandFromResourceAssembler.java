package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.UpdatePlanCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources.UpdatePlanResource;

/**
 * Assembler to create {@link UpdatePlanCommand} from {@link UpdatePlanResource}.
 */
public final class UpdatePlanCommandFromResourceAssembler {

    private UpdatePlanCommandFromResourceAssembler() {
    }

    public static UpdatePlanCommand toCommandFromResource(Long planId, UpdatePlanResource resource) {
        return new UpdatePlanCommand(
                planId,
                resource.name(),
                resource.priceAmount(),
                resource.priceCurrency(),
                resource.description(),
                resource.maxActiveTours()
        );
    }
}
