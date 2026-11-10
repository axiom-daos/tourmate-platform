package axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.repositories;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.valueobjects.SubscriptionStatus;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link SubscriptionPersistenceEntity}.
 */
@Repository
public interface SubscriptionPersistenceRepository extends JpaRepository<SubscriptionPersistenceEntity, Long> {

    List<SubscriptionPersistenceEntity> findByAgencyId(Long agencyId);

    Optional<SubscriptionPersistenceEntity> findFirstByAgencyIdAndStatus(Long agencyId, SubscriptionStatus status);
}
