package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.acl;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.queryservices.PlanQueryService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.queryservices.SubscriptionQueryService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetActiveSubscriptionByAgencyIdQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetPlanByIdQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.acl.SubscriptionsContextFacade;
import org.springframework.stereotype.Service;

/**
 * Application-layer implementation of the Subscriptions ACL facade.
 *
 * <p>Delegates strictly to local subscription and plan query services to provide
 * a decoupled integration surface for external bounded contexts.</p>
 */
@Service
public class SubscriptionsContextFacadeImpl implements SubscriptionsContextFacade {

    private final SubscriptionQueryService subscriptionQueryService;
    private final PlanQueryService planQueryService;

    public SubscriptionsContextFacadeImpl(
            SubscriptionQueryService subscriptionQueryService,
            PlanQueryService planQueryService
    ) {
        this.subscriptionQueryService = subscriptionQueryService;
        this.planQueryService = planQueryService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasActiveSubscription(Long agencyId) {
        if (agencyId == null || agencyId < 1) {
            return false;
        }
        return subscriptionQueryService.handle(new GetActiveSubscriptionByAgencyIdQuery(agencyId)).isPresent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int fetchMaxActiveToursQuotaByAgencyId(Long agencyId) {
        if (agencyId == null || agencyId < 1) {
            return 0;
        }
        return subscriptionQueryService.handle(new GetActiveSubscriptionByAgencyIdQuery(agencyId))
                .flatMap(subscription -> planQueryService.handle(new GetPlanByIdQuery(subscription.getPlanId())))
                .map(plan -> plan.getMaxActiveTours() != null ? plan.getMaxActiveTours() : 0)
                .orElse(0);
    }
}
