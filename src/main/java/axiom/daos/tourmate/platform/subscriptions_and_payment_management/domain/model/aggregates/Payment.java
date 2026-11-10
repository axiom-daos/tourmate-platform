package axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates;

import axiom.daos.tourmate.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Money;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.valueobjects.PaymentStatus;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

import java.util.Date;
import java.util.Objects;

/**
 * Payment aggregate root.
 *
 * <p>
 * Represents a financial ledger entry recording payment transactions performed
 * by agencies for subscription renewals, activations, or adjustments.
 * </p>
 */
@Getter
public class Payment extends AbstractDomainAggregateRoot<Payment> {

    /**
     * Unique identifier for the payment transaction.
     */
    private Long id;

    /**
     * The agency identifier making the payment.
     */
    private Long agencyId;

    /**
     * The plan identifier associated with this payment transaction.
     */
    private Long planId;

    /**
     * The monetary amount charged.
     */
    private Money amount;

    /**
     * The settlement status of the transaction.
     */
    private PaymentStatus status;

    /**
     * Timestamp when the payment transaction was created or requested.
     */
    private Date requestedAt;

    /**
     * Reference voucher or transaction code.
     */
    private String transactionReference;

    /**
     * Default constructor for framework initialization.
     */
    public Payment() {
        this.status = PaymentStatus.PENDING;
        this.transactionReference = Strings.EMPTY;
    }

    /**
     * Creates a new Payment record.
     *
     * @param agencyId The agency identifier
     * @param planId The plan identifier
     * @param amount The monetary amount
     * @param transactionReference External or internal voucher reference code
     */
    public Payment(Long agencyId, Long planId, Money amount, String transactionReference) {
        this.agencyId = validateId(agencyId, "Agency id");
        this.planId = validateId(planId, "Plan id");
        this.amount = Objects.requireNonNull(amount, "Amount is required");
        this.status = PaymentStatus.PAID;
        this.requestedAt = new Date();
        this.transactionReference = transactionReference != null && !transactionReference.isBlank()
                ? transactionReference.trim()
                : generateDefaultReference();
    }

    /**
     * Reconstitutes a Payment instance from persistent storage.
     *
     * @param id The persisted identifier
     * @param agencyId The agency identifier
     * @param planId The plan identifier
     * @param amount The monetary amount
     * @param status Transaction settlement status
     * @param requestedAt Request timestamp
     * @param transactionReference Reference voucher code
     * @return Reconstituted Payment instance
     */
    public static Payment reconstitute(
            Long id,
            Long agencyId,
            Long planId,
            Money amount,
            PaymentStatus status,
            Date requestedAt,
            String transactionReference
    ) {
        var payment = new Payment();
        payment.id = id;
        payment.agencyId = agencyId;
        payment.planId = planId;
        payment.amount = amount;
        payment.status = status != null ? status : PaymentStatus.PAID;
        payment.requestedAt = requestedAt != null ? requestedAt : new Date();
        payment.transactionReference = transactionReference != null ? transactionReference : Strings.EMPTY;
        return payment;
    }

    /**
     * Marks transaction as settled and paid.
     */
    public void markAsPaid() {
        this.status = PaymentStatus.PAID;
    }

    /**
     * Marks transaction as rejected.
     */
    public void reject() {
        this.status = PaymentStatus.REJECTED;
    }

    private Long validateId(Long id, String fieldName) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException(fieldName + " must be at least 1");
        }
        return id;
    }

    private String generateDefaultReference() {
        return "TX-" + System.currentTimeMillis();
    }
}
