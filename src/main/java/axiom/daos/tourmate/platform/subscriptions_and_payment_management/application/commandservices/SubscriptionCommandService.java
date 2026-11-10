package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Subscription;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CancelSubscriptionCommand;
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
     * Handles cancellation of an active subscription.
     *
     * @param command Command payload
     * @return Result containing updated subscription on success, or ApplicationError
     */
    Result<Subscription, ApplicationError> handle(CancelSubscriptionCommand command);
}
