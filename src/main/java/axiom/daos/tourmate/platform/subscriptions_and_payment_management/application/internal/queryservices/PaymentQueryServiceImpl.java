package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.internal.queryservices;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.queryservices.PaymentQueryService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Payment;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetAllPaymentsQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetPaymentByIdQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetPaymentsByAgencyIdQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.repositories.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link PaymentQueryService}.
 */
@Service
public class PaymentQueryServiceImpl implements PaymentQueryService {

    private final PaymentRepository paymentRepository;

    public PaymentQueryServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Optional<Payment> handle(GetPaymentByIdQuery query) {
        return paymentRepository.findById(query.paymentId());
    }

    @Override
    public List<Payment> handle(GetAllPaymentsQuery query) {
        return paymentRepository.findAll();
    }

    @Override
    public List<Payment> handle(GetPaymentsByAgencyIdQuery query) {
        return paymentRepository.findByAgencyId(query.agencyId());
    }
}
