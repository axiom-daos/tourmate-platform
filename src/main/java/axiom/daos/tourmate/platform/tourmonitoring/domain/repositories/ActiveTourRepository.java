package axiom.daos.tourmate.platform.tourmonitoring.domain.repositories;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.ActiveTour;

import java.util.List;
import java.util.Optional;

public interface ActiveTourRepository {
    Optional<ActiveTour> findById(Long id);
    List<ActiveTour> findAll();
    boolean existsById(Long id);
    void deleteById(Long id);
}
