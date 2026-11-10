package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.internal.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.commandservices.SubscriptionCommandService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Subscription;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CancelSubscriptionCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CreateSubscriptionCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.valueobjects.SubscriptionStatus;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories.PlanRepository;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link SubscriptionCommandService}.
 */
@Service
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;

    public SubscriptionCommandServiceImpl(
            SubscriptionRepository subscriptionRepository,
            PlanRepository planRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
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
