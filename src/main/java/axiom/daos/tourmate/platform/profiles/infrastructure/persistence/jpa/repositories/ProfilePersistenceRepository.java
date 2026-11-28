package axiom.daos.tourmate.platform.profiles.infrastructure.persistence.jpa.repositories;

import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.EmailAddress;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.UserId;
import axiom.daos.tourmate.platform.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link ProfilePersistenceEntity}.
 */
@Repository
public interface ProfilePersistenceRepository extends JpaRepository<ProfilePersistenceEntity, Long> {

    Optional<ProfilePersistenceEntity> findByEmail(EmailAddress email);

    Optional<ProfilePersistenceEntity> findByUserId(UserId userId);

    boolean existsByEmail(EmailAddress email);

    boolean existsByEmailAndIdIsNot(EmailAddress email, Long id);

    boolean existsByUserId(UserId userId);
}
