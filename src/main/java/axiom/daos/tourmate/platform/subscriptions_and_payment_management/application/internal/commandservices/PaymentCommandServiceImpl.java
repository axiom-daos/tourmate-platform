package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.internal.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Currency;
import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Money;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.commandservices.PaymentCommandService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Payment;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.ConfirmPaymentCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.commands.CreatePaymentCommand;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories.PaymentRepository;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories.PlanRepository;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link PaymentCommandService}.
 */
@Service
public class PaymentCommandServiceImpl implements PaymentCommandService {

    private final PaymentRepository paymentRepository;
    private final PlanRepository planRepository;

    public PaymentCommandServiceImpl(
            PaymentRepository paymentRepository,
            PlanRepository planRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.planRepository = planRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(CreatePaymentCommand command) {
        if (!planRepository.existsById(command.planId())) {
            return Result.failure(ApplicationError.notFound("Plan", command.planId().toString()));
        }

        try {
            var money = new Money(command.amount(), new Currency(command.currency()));
            var payment = new Payment(command.agencyId(), command.planId(), money, command.transactionReference());
            var savedPayment = paymentRepository.save(payment);
            return Result.success(savedPayment.getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Payment", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-payment", e.getMessage()));
        }
    }

    @Override
    public Result<Payment, ApplicationError> handle(ConfirmPaymentCommand command) {
        var existing = paymentRepository.findById(command.paymentId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Payment", command.paymentId().toString()));
        }

        var payment = existing.get();
        if (!payment.isPending()) {
            return Result.failure(ApplicationError.conflict(
                    "Payment",
                    "Payment %d is already in status %s and cannot be confirmed".formatted(payment.getId(), payment.getStatus())
            ));
        }

        try {
            payment.confirm();
            var savedPayment = paymentRepository.save(payment);
            return Result.success(savedPayment);
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.conflict("Payment", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("confirm-payment", e.getMessage()));
        }
    }
}
