package axiom.daos.tourmate.platform.tour_management.application.queryservices;

import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.Tour;
import axiom.daos.tourmate.platform.tour_management.domain.model.queries.ExistsByIdQuery;
import axiom.daos.tourmate.platform.tour_management.domain.model.queries.GetAllToursQuery;
import axiom.daos.tourmate.platform.tour_management.domain.model.queries.GetTourByIdQuery;

import java.util.List;
import java.util.Optional;

public interface TourQueryService {

    Optional<Tour> handle(GetTourByIdQuery query);

    List<Tour> handle(GetAllToursQuery query);

}
