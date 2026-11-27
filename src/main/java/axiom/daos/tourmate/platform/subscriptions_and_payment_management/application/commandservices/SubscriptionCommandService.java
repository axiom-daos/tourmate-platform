package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Subscription;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.ActivateSubscriptionOnPaymentCompletedCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CancelSubscriptionCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.ChangeSubscriptionPlanCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CreateSubscriptionCommand;

/**
 * Command application service interface for the {@link Subscription} aggregate.
 */
public interface SubscriptionCommandService {

    /**
     * Handles subscription creation/activation for an agency.
     *
     * @param command Command payload
     * @return Result containing new subscription ID on success, or ApplicationError
     */
    Result<Long, ApplicationError> handle(CreateSubscriptionCommand command);

    /**
     * Handles automatic subscription activation or plan update triggered when a payment is completed.
     *
     * @param command Command payload originating from {@code PaymentCompletedEvent}
     * @return Result containing the active subscription on success, or ApplicationError
     */
    Result<Subscription, ApplicationError> handle(ActivateSubscriptionOnPaymentCompletedCommand command);

    /**
     * Handles changing the commercial plan of an active subscription while enforcing plan tour quotas via ACL.
     *
     * @param command Command payload
     * @return Result containing updated subscription on success, or ApplicationError
     */
    Result<Subscription, ApplicationError> handle(ChangeSubscriptionPlanCommand command);

    /**
     * Handles cancellation of an active subscription.
     *
     * @param command Command payload
     * @return Result containing updated subscription on success, or ApplicationError
     */
    Result<Subscription, ApplicationError> handle(CancelSubscriptionCommand command);
}
