package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.internal.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Currency;
import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Money;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.commandservices.PlanCommandService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Plan;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CreatePlanCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.DeletePlanCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.UpdatePlanCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories.PlanRepository;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link PlanCommandService}.
 */
@Service
public class PlanCommandServiceImpl implements PlanCommandService {

    private final PlanRepository planRepository;

    public PlanCommandServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(CreatePlanCommand command) {
        if (planRepository.existsByName(command.name())) {
            return Result.failure(ApplicationError.conflict("Plan", "A plan with name '%s' already exists".formatted(command.name())));
        }

        try {
            var money = new Money(command.priceAmount(), new Currency(command.priceCurrency()));
            var plan = new Plan(command.name(), money, command.description(), command.maxActiveTours());
            var savedPlan = planRepository.save(plan);
            return Result.success(savedPlan.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Plan", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-plan", e.getMessage()));
        }
    }

    @Override
    public Result<Plan, ApplicationError> handle(UpdatePlanCommand command) {
        var existingPlan = planRepository.findById(command.planId());
        if (existingPlan.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Plan", command.planId().toString()));
        }

        if (planRepository.existsByNameAndIdIsNot(command.name(), command.planId())) {
            return Result.failure(ApplicationError.conflict("Plan", "Another plan with name '%s' already exists".formatted(command.name())));
        }

        try {
            var plan = existingPlan.get();
            var money = new Money(command.priceAmount(), new Currency(command.priceCurrency()));
            plan.updateInformation(command.name(), money, command.description(), command.maxActiveTours());
            var savedPlan = planRepository.save(plan);
            return Result.success(savedPlan);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Plan", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("update-plan", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(DeletePlanCommand command) {
        if (!planRepository.existsById(command.planId())) {
            return Result.failure(ApplicationError.notFound("Plan", command.planId().toString()));
        }

        try {
            planRepository.deleteById(command.planId());
            return Result.success(command.planId());
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("delete-plan", e.getMessage()));
        }
    }
}
