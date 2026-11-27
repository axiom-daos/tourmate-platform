package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.internal.eventhandlers;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.commandservices.SubscriptionCommandService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Subscription;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.ActivateSubscriptionOnPaymentCompletedCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.events.PaymentCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Event handler for {@link PaymentCompletedEvent}.
 *
 * <p>Reacts automatically when a payment transaction is confirmed as settled ({@code PAID})
 * by activating or updating the agency's subscription to the paid commercial plan.</p>
 */
@Service
@Slf4j
public class PaymentCompletedEventHandler {

    private final SubscriptionCommandService subscriptionCommandService;

    /**
     * Constructor.
     *
     * @param subscriptionCommandService the subscription command service
     */
    public PaymentCompletedEventHandler(SubscriptionCommandService subscriptionCommandService) {
        this.subscriptionCommandService = subscriptionCommandService;
    }

    /**
     * Handles {@link PaymentCompletedEvent} by dispatching
     * {@link ActivateSubscriptionOnPaymentCompletedCommand}.
     *
     * @param event the {@link PaymentCompletedEvent} emitted upon payment confirmation
     */
    @EventListener(PaymentCompletedEvent.class)
    public void on(PaymentCompletedEvent event) {
        var command = new ActivateSubscriptionOnPaymentCompletedCommand(
                event.agencyId(),
                event.planId(),
                event.paymentId()
        );
        var result = subscriptionCommandService.handle(command);

        if (result instanceof Result.Failure<Subscription, ApplicationError>(var error)) {
            log.warn("Failed to activate subscription for agency {} after payment {}: {}",
                    event.agencyId(), event.paymentId(), error.message());
        } else {
            log.info("Subscription synchronized for agency {} on plan {} after payment {}",
                    event.agencyId(), event.planId(), event.paymentId());
        }
    }
}
