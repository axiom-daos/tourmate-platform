package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.queryservices;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Plan;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetAllPlansQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetPlanByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Query application service interface for the {@link Plan} aggregate.
 */
public interface PlanQueryService {

    /**
     * Handles retrieval of a single plan by its identifier.
     *
     * @param query Query containing plan id
     * @return Optional containing Plan if found
     */
    Optional<Plan> handle(GetPlanByIdQuery query);

    /**
     * Handles retrieval of all commercial subscription plans.
     *
     * @param query Marker query
     * @return List of all plans
     */
    List<Plan> handle(GetAllPlansQuery query);
}
