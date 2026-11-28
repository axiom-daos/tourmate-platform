package axiom.daos.tourmate.platform.profiles.domain.repositories;

import axiom.daos.tourmate.platform.profiles.domain.model.aggregates.Profile;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.EmailAddress;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for {@link Profile} aggregates.
 */
public interface ProfileRepository {

    Profile save(Profile profile);

    Optional<Profile> findById(Long id);

    Optional<Profile> findByEmail(EmailAddress email);

    Optional<Profile> findByUserId(UserId userId);

    List<Profile> findAll();

    boolean existsById(Long id);

    boolean existsByEmail(EmailAddress email);

    boolean existsByEmailAndIdIsNot(EmailAddress email, Long id);

    boolean existsByUserId(UserId userId);

    void deleteById(Long id);
}
