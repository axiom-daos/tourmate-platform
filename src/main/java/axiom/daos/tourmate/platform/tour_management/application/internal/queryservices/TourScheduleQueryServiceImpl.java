package axiom.daos.tourmate.platform.tour_management.application.internal.queryservices;

import axiom.daos.tourmate.platform.tour_management.application.queryservices.TourScheduleQueryService;
import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.TourSchedule;
import axiom.daos.tourmate.platform.tour_management.domain.model.queries.GetAllTourSchedulesQuery;
import axiom.daos.tourmate.platform.tour_management.domain.model.queries.GetTourScheduleByIdQuery;
import axiom.daos.tourmate.platform.tour_management.domain.repositories.TourScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TourScheduleQueryServiceImpl implements TourScheduleQueryService {

    private final TourScheduleRepository tourScheduleRepository;

    public TourScheduleQueryServiceImpl(TourScheduleRepository tourScheduleRepository) {
        this.tourScheduleRepository = tourScheduleRepository;
    }

    @Override
    public Optional<TourSchedule> handle(GetTourScheduleByIdQuery query) {
        return this.tourScheduleRepository.findById(query.id());
    }

    @Override
    public List<TourSchedule> handle(GetAllTourSchedulesQuery query) {
        return this.tourScheduleRepository.findAll();
    }
}
