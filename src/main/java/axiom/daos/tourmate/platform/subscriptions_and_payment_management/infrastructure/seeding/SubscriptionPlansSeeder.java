package axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.seeding;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.commandservices.PlanCommandService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CreatePlanCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories.PlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Database seeder that bootstraps standard commercial TourMate subscription plans
 * on application startup if not already seeded.
 */
@Component
public class SubscriptionPlansSeeder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionPlansSeeder.class);

    private final PlanRepository planRepository;
    private final PlanCommandService planCommandService;

    public SubscriptionPlansSeeder(PlanRepository planRepository, PlanCommandService planCommandService) {
        this.planRepository = planRepository;
        this.planCommandService = planCommandService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedPlans() {
        seedPlanIfNotExists("Explorer", new BigDecimal("29.00"), "USD",
                "Starter tier for boutique operators. Up to 5 active tours with essential tracking.", 5);

        seedPlanIfNotExists("Pro Trekker", new BigDecimal("79.00"), "USD",
                "Professional tier with up to 20 active tours, custom branding, and priority support.", 20);

        seedPlanIfNotExists("Enterprise Expedition", new BigDecimal("199.00"), "USD",
                "Comprehensive enterprise tier with up to 100 active tours and dedicated support.", 100);
    }

    private void seedPlanIfNotExists(String name, BigDecimal price, String currency, String description, int maxActiveTours) {
        if (!planRepository.existsByName(name)) {
            var command = new CreatePlanCommand(name, price, currency, description, maxActiveTours);
            var result = planCommandService.handle(command);
            if (result instanceof Result.Success<Long, ApplicationError> success) {
                LOGGER.info("Seeded subscription plan '{}' with id {}", name, success.value());
            } else if (result instanceof Result.Failure<Long, ApplicationError> failure) {
                LOGGER.warn("Failed to seed subscription plan '{}': {}", name, failure.error().message());
            }
        }
    }
}
