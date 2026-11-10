package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CreatePaymentCommand;

/**
 * Command application service interface for the {@link axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Payment} aggregate.
 */
public interface PaymentCommandService {

    /**
     * Handles recording a new subscription payment transaction.
     *
     * @param command Command payload
     * @return Result containing generated payment ID on success, or ApplicationError
     */
    Result<Long, ApplicationError> handle(CreatePaymentCommand command);
}
