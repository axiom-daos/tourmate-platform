package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.internal.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Currency;
import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Money;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.internal.outboundservices.acl.ExternalTourManagementService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Plan;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Subscription;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.ActivateSubscriptionOnPaymentCompletedCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.ChangeSubscriptionPlanCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.valueobjects.SubscriptionStatus;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories.PlanRepository;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionCommandServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private ExternalTourManagementService externalTourManagementService;

    @InjectMocks
    private SubscriptionCommandServiceImpl service;

    @Test
    void activateOnPaymentCompletedCreatesSubscriptionWhenNoneIsActive() {
        when(planRepository.existsById(2L)).thenReturn(true);
        when(subscriptionRepository.findFirstByAgencyIdAndStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var result = service.handle(new ActivateSubscriptionOnPaymentCompletedCommand(1L, 2L, 10L));

        assertInstanceOf(Result.Success.class, result);
        var sub = ((Result.Success<Subscription, ApplicationError>) result).value();
        assertEquals(1L, sub.getAgencyId());
        assertEquals(2L, sub.getPlanId());
        assertTrue(sub.isActive());
    }

    @Test
    void changePlanBlocksDowngradeWhenActiveToursExceedTargetPlanQuota() {
        var currentSubscription = Subscription.reconstitute(5L, 1L, 2L, SubscriptionStatus.ACTIVE, new Date(), null);
        var explorerPlan = Plan.reconstitute(1L, "Explorer", new Money(new BigDecimal("29.00"), new Currency("USD")), "Starter", 5);

        when(subscriptionRepository.findById(5L)).thenReturn(Optional.of(currentSubscription));
        when(planRepository.findById(1L)).thenReturn(Optional.of(explorerPlan));
        when(externalTourManagementService.fetchActiveToursCountByAgencyId(1L)).thenReturn(8);

        var result = service.handle(new ChangeSubscriptionPlanCommand(5L, 1L));

        assertInstanceOf(Result.Failure.class, result);
        var error = ((Result.Failure<Subscription, ApplicationError>) result).error();
        assertEquals("BUSINESS_RULE_VIOLATION", error.code());
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void changePlanSucceedsWhenActiveToursWithinTargetPlanQuota() {
        var currentSubscription = Subscription.reconstitute(5L, 1L, 2L, SubscriptionStatus.ACTIVE, new Date(), null);
        var explorerPlan = Plan.reconstitute(1L, "Explorer", new Money(new BigDecimal("29.00"), new Currency("USD")), "Starter", 5);

        when(subscriptionRepository.findById(5L)).thenReturn(Optional.of(currentSubscription));
        when(planRepository.findById(1L)).thenReturn(Optional.of(explorerPlan));
        when(externalTourManagementService.fetchActiveToursCountByAgencyId(1L)).thenReturn(3);
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = service.handle(new ChangeSubscriptionPlanCommand(5L, 1L));

        assertInstanceOf(Result.Success.class, result);
        var updated = ((Result.Success<Subscription, ApplicationError>) result).value();
        assertEquals(1L, updated.getPlanId());
        verify(subscriptionRepository).save(currentSubscription);
    }
}
