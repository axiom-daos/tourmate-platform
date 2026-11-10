package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Plan;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for the {@link Plan} aggregate.
 */
public interface PlanRepository {

    /**
     * Find a plan by its unique identifier.
     *
     * @param id The plan id
     * @return Optional containing the plan if found
     */
    Optional<Plan> findById(Long id);

    /**
     * Retrieve all available plans.
     *
     * @return List of all plans
     */
    List<Plan> findAll();

    /**
     * Find a plan by its exact commercial name.
     *
     * @param name The plan name
     * @return Optional containing the plan if found
     */
    Optional<Plan> findByName(String name);

    /**
     * Check if a plan exists by its identifier.
     *
     * @param id The plan id
     * @return True if exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Check if a plan already exists with the given name.
     *
     * @param name The plan name
     * @return True if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Check if another plan exists with the given name excluding an identifier.
     *
     * @param name The plan name
     * @param id   The plan id to exclude
     * @return True if another plan exists with this name
     */
    boolean existsByNameAndIdIsNot(String name, Long id);

    /**
     * Save a plan (create or update).
     *
     * @param plan The plan aggregate
     * @return The persisted plan aggregate
     */
    Plan save(Plan plan);

    /**
     * Delete a plan aggregate.
     *
     * @param plan The plan aggregate to delete
     */
    void delete(Plan plan);

    /**
     * Delete a plan by its identifier.
     *
     * @param id The plan id
     */
    void deleteById(Long id);
}
