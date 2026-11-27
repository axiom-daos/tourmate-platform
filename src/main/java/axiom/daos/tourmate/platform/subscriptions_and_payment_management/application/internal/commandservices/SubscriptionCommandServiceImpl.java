package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.internal.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.commandservices.SubscriptionCommandService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.internal.outboundservices.acl.ExternalTourManagementService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Subscription;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.ActivateSubscriptionOnPaymentCompletedCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CancelSubscriptionCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.ChangeSubscriptionPlanCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CreateSubscriptionCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.valueobjects.SubscriptionStatus;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories.PlanRepository;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Implementation of {@link SubscriptionCommandService}.
 */
@Service
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final ExternalTourManagementService externalTourManagementService;

    public SubscriptionCommandServiceImpl(
            SubscriptionRepository subscriptionRepository,
            PlanRepository planRepository,
            ExternalTourManagementService externalTourManagementService
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.externalTourManagementService = externalTourManagementService;
    }

    @Override
    public Result<Long, ApplicationError> handle(CreateSubscriptionCommand command) {
        if (!planRepository.existsById(command.planId())) {
            return Result.failure(ApplicationError.notFound("Plan", command.planId().toString()));
        }

        try {
            var activeSubscription = subscriptionRepository.findFirstByAgencyIdAndStatus(command.agencyId(), SubscriptionStatus.ACTIVE);
            activeSubscription.ifPresent(sub -> {
                sub.cancel();
                subscriptionRepository.save(sub);
            });

            var subscription = new Subscription(command.agencyId(), command.planId());
            var savedSubscription = subscriptionRepository.save(subscription);
            return Result.success(savedSubscription.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Subscription", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-subscription", e.getMessage()));
        }
    }

    @Override
    public Result<Subscription, ApplicationError> handle(ActivateSubscriptionOnPaymentCompletedCommand command) {
        if (!planRepository.existsById(command.planId())) {
            return Result.failure(ApplicationError.notFound("Plan", command.planId().toString()));
        }

        try {
            var activeSubscription = subscriptionRepository
                    .findFirstByAgencyIdAndStatus(command.agencyId(), SubscriptionStatus.ACTIVE);

            if (activeSubscription.isPresent()) {
                var current = activeSubscription.get();
                if (Objects.equals(current.getPlanId(), command.planId())) {
                    return Result.success(current);
                }
                current.changePlan(command.planId());
                return Result.success(subscriptionRepository.save(current));
            }

            var subscription = new Subscription(command.agencyId(), command.planId());
            return Result.success(subscriptionRepository.save(subscription));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Subscription", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("activate-subscription-on-payment", e.getMessage()));
        }
    }

    @Override
    public Result<Subscription, ApplicationError> handle(ChangeSubscriptionPlanCommand command) {
        var existing = subscriptionRepository.findById(command.subscriptionId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Subscription", command.subscriptionId().toString()));
        }

        var subscription = existing.get();
        if (!subscription.isActive()) {
            return Result.failure(ApplicationError.conflict(
                    "Subscription",
                    "Subscription %d is not active and cannot change plans".formatted(subscription.getId())
            ));
        }

        var targetPlanOpt = planRepository.findById(command.newPlanId());
        if (targetPlanOpt.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Plan", command.newPlanId().toString()));
        }

        if (Objects.equals(subscription.getPlanId(), command.newPlanId())) {
            return Result.failure(ApplicationError.conflict(
                    "Subscription",
                    "Subscription %d is already on plan %d".formatted(subscription.getId(), command.newPlanId())
            ));
        }

        var targetPlan = targetPlanOpt.get();
        var currentActiveTours = externalTourManagementService.fetchActiveToursCountByAgencyId(subscription.getAgencyId());
        if (currentActiveTours > targetPlan.getMaxActiveTours()) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "SubscriptionPlanQuota",
                    "Cannot change to plan '%s' (max %d active tours) because agency %d currently has %d active tours"
                            .formatted(targetPlan.getName(), targetPlan.getMaxActiveTours(), subscription.getAgencyId(), currentActiveTours)
            ));
        }

        try {
            subscription.changePlan(command.newPlanId());
            var savedSubscription = subscriptionRepository.save(subscription);
            return Result.success(savedSubscription);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Subscription", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.conflict("Subscription", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("change-subscription-plan", e.getMessage()));
        }
    }

    @Override
    public Result<Subscription, ApplicationError> handle(CancelSubscriptionCommand command) {
        var existing = subscriptionRepository.findById(command.subscriptionId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Subscription", command.subscriptionId().toString()));
        }

        try {
            var subscription = existing.get();
            subscription.cancel();
            var savedSubscription = subscriptionRepository.save(subscription);
            return Result.success(savedSubscription);
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("cancel-subscription", e.getMessage()));
        }
    }
}
