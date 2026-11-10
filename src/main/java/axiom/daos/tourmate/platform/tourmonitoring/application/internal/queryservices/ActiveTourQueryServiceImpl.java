package axiom.daos.tourmate.platform.tourmonitoring.application.internal.queryservices;

import axiom.daos.tourmate.platform.tourmonitoring.application.queryservices.ActiveTourQueryService;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.ActiveTour;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.queries.GetActiveTourByIdQuery;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.queries.GetAllActiveToursQuery;
import axiom.daos.tourmate.platform.tourmonitoring.domain.repositories.ActiveTourRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActiveTourQueryServiceImpl implements ActiveTourQueryService {
    private final ActiveTourRepository activeTourRepository;

    public ActiveTourQueryServiceImpl(ActiveTourRepository activeTourRepository) {
        this.activeTourRepository = activeTourRepository;
    }

    @Override
    public Optional<ActiveTour> handle(GetActiveTourByIdQuery query) {
        return activeTourRepository.findById(query.activeTourId());
    }

    @Override
    public List<ActiveTour> handle(GetAllActiveToursQuery query) {
        return activeTourRepository.findAll();
    }

}