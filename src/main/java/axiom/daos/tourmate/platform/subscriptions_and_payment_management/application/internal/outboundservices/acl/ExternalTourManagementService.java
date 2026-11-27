package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.internal.outboundservices.acl;

import axiom.daos.tourmate.platform.tour_management.interfaces.acl.TourManagementContextFacade;
import org.springframework.stereotype.Service;

/**
 * Outbound ACL service used by the {@code subscriptions_and_payment_management} bounded context
 * to query Tour Management capabilities without coupling to its internal domain model.
 */
@Service
public class ExternalTourManagementService {

    private final TourManagementContextFacade tourManagementContextFacade;

    /**
     * Creates the outbound ACL service backed by the Tour Management context facade.
     *
     * @param tourManagementContextFacade Tour Management bounded-context facade
     */
    public ExternalTourManagementService(TourManagementContextFacade tourManagementContextFacade) {
        this.tourManagementContextFacade = tourManagementContextFacade;
    }

    /**
     * Fetches the count of active (non-unavailable) tours currently owned by an agency.
     *
     * @param agencyId the agency identifier
     * @return current active tours count for the agency
     */
    public int fetchActiveToursCountByAgencyId(Long agencyId) {
        return tourManagementContextFacade.countActiveToursByAgencyId(agencyId);
    }

    /**
     * Checks whether the agency's current tour count exceeds a target plan's tour quota.
     *
     * @param agencyId       the agency identifier
     * @param maxActiveTours maximum active tours permitted by the target plan
     * @return {@code true} if current active tours exceed {@code maxActiveTours}
     */
    public boolean exceedsPlanQuota(Long agencyId, int maxActiveTours) {
        return fetchActiveToursCountByAgencyId(agencyId) > maxActiveTours;
    }
}
