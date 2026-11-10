package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CreatePlanCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources.CreatePlanResource;

/**
 * Assembler to create {@link CreatePlanCommand} from {@link CreatePlanResource}.
 */
public final class CreatePlanCommandFromResourceAssembler {

    private CreatePlanCommandFromResourceAssembler() {
    }

    public static CreatePlanCommand toCommandFromResource(CreatePlanResource resource) {
        return new CreatePlanCommand(
                resource.name(),
                resource.priceAmount(),
                resource.priceCurrency(),
                resource.description(),
                resource.maxActiveTours()
        );
    }
}
