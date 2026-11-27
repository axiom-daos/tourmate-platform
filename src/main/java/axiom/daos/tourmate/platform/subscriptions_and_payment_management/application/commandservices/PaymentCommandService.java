package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Payment;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.ConfirmPaymentCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CreatePaymentCommand;

/**
 * Command application service interface for the {@link Payment} aggregate.
 */
public interface PaymentCommandService {

    /**
     * Handles recording a new subscription payment transaction.
     *
     * @param command Command payload
     * @return Result containing generated payment ID on success, or ApplicationError
     */
    Result<Long, ApplicationError> handle(CreatePaymentCommand command);

    /**
     * Handles confirming a pending payment transaction, emitting a {@code PaymentCompletedEvent}.
     *
     * @param command Command payload containing payment identifier
     * @return Result containing confirmed payment aggregate on success, or ApplicationError
     */
    Result<Payment, ApplicationError> handle(ConfirmPaymentCommand command);
}
