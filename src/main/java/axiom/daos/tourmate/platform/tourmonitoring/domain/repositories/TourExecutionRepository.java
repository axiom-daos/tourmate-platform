package axiom.daos.tourmate.platform.tourmonitoring.domain.repositories;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.TourExecution;

import java.util.Optional;

/**
 * Domain repository port for {@link TourExecution} aggregates.
 */
public interface TourExecutionRepository {

    Optional<TourExecution> findById(Long id);

    TourExecution save(TourExecution execution);

    boolean existsById(Long id);
}
