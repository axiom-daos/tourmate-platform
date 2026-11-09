package axiom.daos.tourmate.platform.tour_management.domain.repositories;

import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.Tour;

import java.util.List;
import java.util.Optional;

public interface TourRepository {

    Optional<Tour> findById(Long id);

    List<Tour> findAll();

    boolean existsById(Long id);

    Tour save(Tour tour);

    void deleteById(Long id);
}
