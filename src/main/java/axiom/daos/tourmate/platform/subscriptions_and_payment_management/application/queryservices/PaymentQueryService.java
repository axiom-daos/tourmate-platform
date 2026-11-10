package axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.queryservices;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Payment;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetAllPaymentsQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetPaymentByIdQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetPaymentsByAgencyIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Query application service interface for the {@link Payment} aggregate.
 */
public interface PaymentQueryService {

    /**
     * Handles retrieval of a payment by its identifier.
     *
     * @param query Query containing payment id
     * @return Optional containing Payment if found
     */
    Optional<Payment> handle(GetPaymentByIdQuery query);

    /**
     * Handles retrieval of all payment transactions.
     *
     * @param query Marker query
     * @return List of all payments
     */
    List<Payment> handle(GetAllPaymentsQuery query);

    /**
     * Handles retrieval of payments made by a given agency.
     *
     * @param query Query containing agency id
     * @return List of payments belonging to agency
     */
    List<Payment> handle(GetPaymentsByAgencyIdQuery query);
}
