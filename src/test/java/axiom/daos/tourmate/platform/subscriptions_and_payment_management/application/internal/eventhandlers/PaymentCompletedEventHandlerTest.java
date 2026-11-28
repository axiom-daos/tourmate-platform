package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.internal.eventhandlers;

import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.commandservices.SubscriptionCommandService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Subscription;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.ActivateSubscriptionOnPaymentCompletedCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.events.PaymentCompletedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentCompletedEventHandlerTest {

    @Mock
    private SubscriptionCommandService subscriptionCommandService;

    @InjectMocks
    private PaymentCompletedEventHandler eventHandler;

    @Test
    void onPaymentCompletedDispatchesActivateSubscriptionCommand() {
        var event = new PaymentCompletedEvent(15L, 3L, 2L, new BigDecimal("79.00"), "USD", "REC-200");
        var activated = new Subscription(3L, 2L);
        when(subscriptionCommandService.handle(any(ActivateSubscriptionOnPaymentCompletedCommand.class)))
                .thenReturn(Result.success(activated));

        eventHandler.on(event);

        var captor = ArgumentCaptor.forClass(ActivateSubscriptionOnPaymentCompletedCommand.class);
        verify(subscriptionCommandService).handle(captor.capture());
        assertEquals(3L, captor.getValue().agencyId());
        assertEquals(2L, captor.getValue().planId());
        assertEquals(15L, captor.getValue().paymentId());
    }
}
