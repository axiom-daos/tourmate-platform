package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Subscription;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources.SubscriptionResource;

/**
 * Assembler to map {@link Subscription} entity to {@link SubscriptionResource}.
 */
public final class SubscriptionResourceFromEntityAssembler {

    private SubscriptionResourceFromEntityAssembler() {
    }

    public static SubscriptionResource toResourceFromEntity(Subscription entity) {
        if (entity == null) {
            return null;
        }
        return new SubscriptionResource(
                entity.getId(),
                entity.getAgencyId(),
                entity.getPlanId(),
                entity.getStatus().name(),
                entity.getActivatedAt(),
                entity.getCancelledAt()
        );
    }
}
