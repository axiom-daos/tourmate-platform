package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.acl;

/**
 * ACL facade that exposes Subscriptions and Payment Management capabilities to other bounded contexts.
 *
 * <p>Provides a simplified integration surface for checking agency subscription status
 * and plan tour quotas without leaking internal domain models.</p>
 */
public interface SubscriptionsContextFacade {

    /**
     * Checks whether the given agency currently holds an active subscription.
     *
     * @param agencyId the agency identifier
     * @return {@code true} if an active subscription exists, {@code false} otherwise
     */
    boolean hasActiveSubscription(Long agencyId);

    /**
     * Fetches the maximum number of concurrent active tours allowed by the agency's active plan.
     *
     * @param agencyId the agency identifier
     * @return maximum active tours quota, or {@code 0} when no active subscription or plan is found
     */
    int fetchMaxActiveToursQuotaByAgencyId(Long agencyId);
}
