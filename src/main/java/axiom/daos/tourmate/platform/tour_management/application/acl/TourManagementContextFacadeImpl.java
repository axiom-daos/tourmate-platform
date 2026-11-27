package axiom.daos.tourmate.platform.tour_management.application.acl;

import axiom.daos.tourmate.platform.tour_management.application.queryservices.TourQueryService;
import axiom.daos.tourmate.platform.tour_management.domain.model.queries.GetAllToursQuery;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourStatus;
import axiom.daos.tourmate.platform.tour_management.interfaces.acl.TourManagementContextFacade;
import org.springframework.stereotype.Service;

/**
 * Application-layer implementation of the Tour Management ACL facade.
 *
 * <p>Delegates strictly to local Tour Management query services to expose
 * agency tour usage metrics without leaking internal domain structures.</p>
 */
@Service
public class TourManagementContextFacadeImpl implements TourManagementContextFacade {

    private final TourQueryService tourQueryService;

    public TourManagementContextFacadeImpl(TourQueryService tourQueryService) {
        this.tourQueryService = tourQueryService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countActiveToursByAgencyId(Long agencyId) {
        if (agencyId == null || agencyId < 1) {
            return 0;
        }
        return (int) tourQueryService.handle(new GetAllToursQuery()).stream()
                .filter(tour -> tour.getAgencyId() != null && agencyId.equals(tour.getAgencyId().value()))
                .filter(tour -> tour.getStatus() != TourStatus.UNAVAILABLE)
                .count();
    }
}
