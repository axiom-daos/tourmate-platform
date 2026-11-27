package axiom.daos.tourmate.platform.tour_management.interfaces.acl;

/**
 * ACL facade that exposes Tour Management read capabilities to other bounded contexts
 * without coupling consumers to internal Tour Management domain models.
 */
public interface TourManagementContextFacade {

    /**
     * Counts non-unavailable tours belonging to a given agency.
     *
     * @param agencyId the agency identifier
     * @return number of active, draft, or published tours owned by the agency
     */
    int countActiveToursByAgencyId(Long agencyId);
}
