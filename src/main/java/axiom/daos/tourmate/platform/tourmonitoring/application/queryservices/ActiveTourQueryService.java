package axiom.daos.tourmate.platform.tourmonitoring.application.queryservices;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.ActiveTour;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.queries.GetActiveTourByIdQuery;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.queries.GetAllActiveToursQuery;

import java.util.List;
import java.util.Optional;

public interface ActiveTourQueryService {
    /**
     * Handles retrieval of a activeTour by id.
     *
     * @param query activeTour-id query
     * @return matching activeTour, if found
     * @see GetActiveTourByIdQuery
     */
    Optional<ActiveTour> handle(GetActiveTourByIdQuery query);

    /**
     * Handles retrieval of all activeTours.
     *
     * @param query query marker
     * @return list of activeTours
     * @see GetAllActiveToursQuery
     */
    List<ActiveTour> handle(GetAllActiveToursQuery query);

}
