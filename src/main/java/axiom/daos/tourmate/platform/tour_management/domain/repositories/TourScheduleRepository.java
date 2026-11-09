package axiom.daos.tourmate.platform.tour_management.domain.repositories;

import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.TourSchedule;

import java.util.List;
import java.util.Optional;

public interface TourScheduleRepository {

    Optional<TourSchedule> findById(Long id);

    List<TourSchedule> findAll();

    boolean existsById(Long id);

    TourSchedule save(TourSchedule tour);

    void deleteById(Long id);
}
