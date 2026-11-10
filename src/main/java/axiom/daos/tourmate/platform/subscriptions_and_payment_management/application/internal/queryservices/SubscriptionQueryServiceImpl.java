package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.internal.queryservices;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.queryservices.SubscriptionQueryService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Subscription;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetAllSubscriptionsQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetSubscriptionByIdQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetSubscriptionsByAgencyIdQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link SubscriptionQueryService}.
 */
@Service
public class SubscriptionQueryServiceImpl implements SubscriptionQueryService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionQueryServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Optional<Subscription> handle(GetSubscriptionByIdQuery query) {
        return subscriptionRepository.findById(query.subscriptionId());
    }

    @Override
    public List<Subscription> handle(GetAllSubscriptionsQuery query) {
        return subscriptionRepository.findAll();
    }

    @Override
    public List<Subscription> handle(GetSubscriptionsByAgencyIdQuery query) {
        return subscriptionRepository.findByAgencyId(query.agencyId());
    }
}
