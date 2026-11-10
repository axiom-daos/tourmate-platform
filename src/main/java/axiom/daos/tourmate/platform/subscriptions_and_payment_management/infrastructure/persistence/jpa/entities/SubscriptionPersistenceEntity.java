package axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.entities;

import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.valueobjects.SubscriptionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * JPA persistence entity for agency subscriptions.
 */
@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "agency_id", nullable = false)
    private Long agencyId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SubscriptionStatus status;

    @Column(name = "activated_at")
    private Date activatedAt;

    @Column(name = "cancelled_at")
    private Date cancelledAt;
}
