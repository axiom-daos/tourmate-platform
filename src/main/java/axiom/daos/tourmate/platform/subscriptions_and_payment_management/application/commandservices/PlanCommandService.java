package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Plan;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CreatePlanCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.DeletePlanCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.UpdatePlanCommand;

/**
 * Command application service interface for the {@link Plan} aggregate.
 */
public interface PlanCommandService {

    /**
     * Handles the creation of a new commercial subscription plan.
     *
     * @param command Command payload
     * @return Result containing generated Plan ID on success, or ApplicationError
     */
    Result<Long, ApplicationError> handle(CreatePlanCommand command);

    /**
     * Handles the modification of an existing plan.
     *
     * @param command Command payload
     * @return Result containing updated Plan aggregate on success, or ApplicationError
     */
    Result<Plan, ApplicationError> handle(UpdatePlanCommand command);

    /**
     * Handles the removal of a plan.
     *
     * @param command Command payload
     * @return Result containing deleted Plan ID on success, or ApplicationError
     */
    Result<Long, ApplicationError> handle(DeletePlanCommand command);
}
