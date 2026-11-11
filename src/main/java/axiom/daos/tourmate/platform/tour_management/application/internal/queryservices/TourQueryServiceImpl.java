package axiom.daos.tourmate.platform.tour_management.application.internal.queryservices;

import axiom.daos.tourmate.platform.tour_management.application.queryservices.TourQueryService;
import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.Tour;
import axiom.daos.tourmate.platform.tour_management.domain.model.queries.GetAllToursQuery;
import axiom.daos.tourmate.platform.tour_management.domain.model.queries.GetTourByIdQuery;
import axiom.daos.tourmate.platform.tour_management.domain.repositories.TourRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TourQueryServiceImpl implements TourQueryService {

    private final TourRepository tourRepository;

    public TourQueryServiceImpl(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    @Override
    public Optional<Tour> handle(GetTourByIdQuery query) {
        return this.tourRepository.findById(query.id());
    }

    @Override
    public List<Tour> handle(GetAllToursQuery query) {
        return this.tourRepository.findAll();
    }
}
