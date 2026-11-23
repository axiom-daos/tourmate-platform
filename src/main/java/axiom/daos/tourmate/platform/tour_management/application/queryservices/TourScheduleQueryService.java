package axiom.daos.tourmate.platform.tour_management.application.queryservices;

import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.TourSchedule;
import axiom.daos.tourmate.platform.tour_management.domain.model.queries.GetAllTourSchedulesQuery;
import axiom.daos.tourmate.platform.tour_management.domain.model.queries.GetTourScheduleByIdQuery;

import java.util.List;
import java.util.Optional;

public interface TourScheduleQueryService {

    Optional<TourSchedule> handle(GetTourScheduleByIdQuery query);

    List<TourSchedule> handle(GetAllTourSchedulesQuery query);
}
