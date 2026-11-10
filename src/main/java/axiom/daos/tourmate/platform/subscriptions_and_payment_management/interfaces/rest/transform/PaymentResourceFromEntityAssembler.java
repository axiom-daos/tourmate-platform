package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Payment;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources.PaymentResource;

/**
 * Assembler to map {@link Payment} entity to {@link PaymentResource}.
 */
public final class PaymentResourceFromEntityAssembler {

    private PaymentResourceFromEntityAssembler() {
    }

    public static PaymentResource toResourceFromEntity(Payment entity) {
        if (entity == null) {
            return null;
        }
        return new PaymentResource(
                entity.getId(),
                entity.getAgencyId(),
                entity.getPlanId(),
                entity.getAmount().amount(),
                entity.getAmount().currency().code(),
                entity.getStatus().name(),
                entity.getTransactionReference(),
                entity.getRequestedAt()
        );
    }
}
