package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates;

import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Currency;
import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Money;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.events.PaymentCompletedEvent;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.valueobjects.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void newPaymentStartsInPendingStatus() {
        var payment = new Payment(1L, 2L, new Money(new BigDecimal("79.00"), new Currency("USD")), "REC-100");

        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertTrue(payment.isPending());
        assertFalse(payment.isPaid());
        assertTrue(payment.domainEvents().isEmpty());
    }

    @Test
    void confirmTransitionsPendingPaymentToPaidAndRegistersDomainEvent() {
        var payment = Payment.reconstitute(
                10L,
                1L,
                2L,
                new Money(new BigDecimal("79.00"), new Currency("USD")),
                PaymentStatus.PENDING,
                new Date(),
                "REC-101"
        );

        payment.confirm();

        assertEquals(PaymentStatus.PAID, payment.getStatus());
        assertTrue(payment.isPaid());
        assertEquals(1, payment.domainEvents().size());

        var event = payment.domainEvents().iterator().next();
        assertInstanceOf(PaymentCompletedEvent.class, event);
        var completed = (PaymentCompletedEvent) event;
        assertEquals(10L, completed.paymentId());
        assertEquals(1L, completed.agencyId());
        assertEquals(2L, completed.planId());
        assertEquals(new BigDecimal("79.00"), completed.amount());
        assertEquals("USD", completed.currency());
        assertEquals("REC-101", completed.transactionReference());
    }

    @Test
    void confirmThrowsWhenPaymentIsAlreadyPaid() {
        var payment = Payment.reconstitute(
                10L,
                1L,
                2L,
                new Money(new BigDecimal("79.00"), new Currency("USD")),
                PaymentStatus.PAID,
                new Date(),
                "REC-102"
        );

        assertThrows(IllegalStateException.class, payment::confirm);
        assertTrue(payment.domainEvents().isEmpty());
    }
}
