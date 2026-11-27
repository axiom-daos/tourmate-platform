package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.events;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Payment;

import java.math.BigDecimal;

/**
 * Domain event published when a {@link Payment} transaction is confirmed and settled as {@code PAID}.
 *
 * <p>Listeners within the {@code subscriptions_and_payment_management} bounded context react to this
 * event to automatically activate or update the corresponding agency subscription without coupling
 * payment processing directly to subscription repositories.</p>
 *
 * @param paymentId            The confirmed payment identifier
 * @param agencyId             The agency identifier that performed the payment
 * @param planId               The commercial plan identifier paid for
 * @param amount               The settled monetary amount
 * @param currency             The ISO currency code
 * @param transactionReference The voucher or transaction reference code
 */
public record PaymentCompletedEvent(
        Long paymentId,
        Long agencyId,
        Long planId,
        BigDecimal amount,
        String currency,
        String transactionReference
) {

    /**
     * Factory method that builds a {@link PaymentCompletedEvent} from a confirmed {@link Payment}.
     *
     * @param payment the confirmed payment aggregate
     * @return a populated {@link PaymentCompletedEvent}
     */
    public static PaymentCompletedEvent from(Payment payment) {
        return new PaymentCompletedEvent(
                payment.getId(),
                payment.getAgencyId(),
                payment.getPlanId(),
                payment.getAmount().amount(),
                payment.getAmount().currency().code(),
                payment.getTransactionReference()
        );
    }
}
