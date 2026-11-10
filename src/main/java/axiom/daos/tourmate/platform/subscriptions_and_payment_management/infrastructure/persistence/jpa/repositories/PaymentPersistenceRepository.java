package axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.repositories;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.entities.PaymentPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link PaymentPersistenceEntity}.
 */
@Repository
public interface PaymentPersistenceRepository extends JpaRepository<PaymentPersistenceEntity, Long> {

    List<PaymentPersistenceEntity> findByAgencyId(Long agencyId);

    List<PaymentPersistenceEntity> findByPlanId(Long planId);
}
