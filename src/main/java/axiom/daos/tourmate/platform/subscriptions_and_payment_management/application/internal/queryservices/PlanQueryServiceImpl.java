package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.internal.queryservices;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.queryservices.PlanQueryService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Plan;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetAllPlansQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetPlanByIdQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link PlanQueryService}.
 */
@Service
public class PlanQueryServiceImpl implements PlanQueryService {

    private final PlanRepository planRepository;

    public PlanQueryServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public Optional<Plan> handle(GetPlanByIdQuery query) {
        return planRepository.findById(query.planId());
    }

    @Override
    public List<Plan> handle(GetAllPlansQuery query) {
        return planRepository.findAll();
    }
}
