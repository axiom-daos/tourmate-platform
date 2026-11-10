package axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.repositories;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.entities.PlanPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link PlanPersistenceEntity}.
 */
@Repository
public interface PlanPersistenceRepository extends JpaRepository<PlanPersistenceEntity, Long> {

    Optional<PlanPersistenceEntity> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdIsNot(String name, Long id);
}
