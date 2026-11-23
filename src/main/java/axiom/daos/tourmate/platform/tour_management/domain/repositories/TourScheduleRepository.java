package axiom.daos.tourmate.platform.tour_management.domain.repositories;

import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.TourSchedule;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.DepartureDateTime;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourId;

import java.util.List;
import java.util.Optional;

public interface TourScheduleRepository {

    Optional<TourSchedule> findById(Long id);

    List<TourSchedule> findAll();

    boolean existsById(Long id);

    boolean existsByTourIdAndDepartureDateTime(TourId tourId, DepartureDateTime departureDateTime);

    TourSchedule save(TourSchedule tourSchedule);

    void deleteById(Long id);
}
