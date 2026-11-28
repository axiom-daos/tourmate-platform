package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.acl;

import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Currency;
import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Money;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.queryservices.PlanQueryService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.queryservices.SubscriptionQueryService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Plan;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Subscription;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetActiveSubscriptionByAgencyIdQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetPlanByIdQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.valueobjects.SubscriptionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionsContextFacadeImplTest {

    @Mock
    private SubscriptionQueryService subscriptionQueryService;

    @Mock
    private PlanQueryService planQueryService;

    @InjectMocks
    private SubscriptionsContextFacadeImpl facade;

    @Test
    void returnsActiveSubscriptionAndPlanQuotaForAgency() {
        var subscription = Subscription.reconstitute(1L, 7L, 2L, SubscriptionStatus.ACTIVE, new Date(), null);
        var plan = Plan.reconstitute(2L, "Pro Trekker", new Money(new BigDecimal("79.00"), new Currency("USD")), "Pro", 20);

        when(subscriptionQueryService.handle(new GetActiveSubscriptionByAgencyIdQuery(7L)))
                .thenReturn(Optional.of(subscription));
        when(planQueryService.handle(new GetPlanByIdQuery(2L)))
                .thenReturn(Optional.of(plan));

        assertTrue(facade.hasActiveSubscription(7L));
        assertEquals(20, facade.fetchMaxActiveToursQuotaByAgencyId(7L));
    }

    @Test
    void returnsFalseAndZeroQuotaWhenAgencyHasNoActiveSubscription() {
        when(subscriptionQueryService.handle(new GetActiveSubscriptionByAgencyIdQuery(99L)))
                .thenReturn(Optional.empty());

        assertFalse(facade.hasActiveSubscription(99L));
        assertEquals(0, facade.fetchMaxActiveToursQuotaByAgencyId(99L));
    }
}
