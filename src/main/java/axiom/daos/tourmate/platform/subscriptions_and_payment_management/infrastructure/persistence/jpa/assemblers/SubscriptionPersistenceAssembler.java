package axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Subscription;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;

/**
 * Static assembler between subscription domain and persistence representations.
 */
public final class SubscriptionPersistenceAssembler {

    private SubscriptionPersistenceAssembler() {
    }

    public static Subscription toDomainFromPersistence(SubscriptionPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }
        return Subscription.reconstitute(
                entity.getId(),
                entity.getAgencyId(),
                entity.getPlanId(),
                entity.getStatus(),
                entity.getActivatedAt(),
                entity.getCancelledAt()
        );
    }

    public static SubscriptionPersistenceEntity toPersistenceFromDomain(Subscription subscription) {
        if (subscription == null) {
            return null;
        }
        var entity = new SubscriptionPersistenceEntity();
        if (subscription.getId() != null) {
            entity.setId(subscription.getId());
        }
        entity.setAgencyId(subscription.getAgencyId());
        entity.setPlanId(subscription.getPlanId());
        entity.setStatus(subscription.getStatus());
        entity.setActivatedAt(subscription.getActivatedAt());
        entity.setCancelledAt(subscription.getCancelledAt());
        return entity;
    }
}
