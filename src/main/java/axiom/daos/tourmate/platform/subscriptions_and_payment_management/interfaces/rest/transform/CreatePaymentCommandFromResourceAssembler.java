package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CreatePaymentCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources.CreatePaymentResource;

/**
 * Assembler to create {@link CreatePaymentCommand} from {@link CreatePaymentResource}.
 */
public final class CreatePaymentCommandFromResourceAssembler {

    private CreatePaymentCommandFromResourceAssembler() {
    }

    public static CreatePaymentCommand toCommandFromResource(CreatePaymentResource resource) {
        return new CreatePaymentCommand(
                resource.agencyId(),
                resource.planId(),
                resource.amount(),
                resource.currency(),
                resource.transactionReference()
        );
    }
}
