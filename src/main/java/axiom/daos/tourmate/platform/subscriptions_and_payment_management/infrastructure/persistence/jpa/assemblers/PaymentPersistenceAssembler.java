package axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Currency;
import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Money;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Payment;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.entities.PaymentPersistenceEntity;

/**
 * Static assembler between payment domain and persistence representations.
 */
public final class PaymentPersistenceAssembler {

    private PaymentPersistenceAssembler() {
    }

    public static Payment toDomainFromPersistence(PaymentPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }
        var currency = new Currency(entity.getCurrency());
        var amount = new Money(entity.getAmount(), currency);
        return Payment.reconstitute(
                entity.getId(),
                entity.getAgencyId(),
                entity.getPlanId(),
                amount,
                entity.getStatus(),
                entity.getRequestedAt(),
                entity.getTransactionReference()
        );
    }

    public static PaymentPersistenceEntity toPersistenceFromDomain(Payment payment) {
        if (payment == null) {
            return null;
        }
        var entity = new PaymentPersistenceEntity();
        if (payment.getId() != null) {
            entity.setId(payment.getId());
        }
        entity.setAgencyId(payment.getAgencyId());
        entity.setPlanId(payment.getPlanId());
        entity.setAmount(payment.getAmount().amount());
        entity.setCurrency(payment.getAmount().currency().code());
        entity.setStatus(payment.getStatus());
        entity.setRequestedAt(payment.getRequestedAt());
        entity.setTransactionReference(payment.getTransactionReference());
        return entity;
    }
}
