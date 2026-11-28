package axiom.daos.tourmate.platform.profiles.infrastructure.persistence.jpa.adapters;

import axiom.daos.tourmate.platform.profiles.domain.model.aggregates.Profile;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.EmailAddress;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.UserId;
import axiom.daos.tourmate.platform.profiles.domain.repositories.ProfileRepository;
import axiom.daos.tourmate.platform.profiles.infrastructure.persistence.jpa.assemblers.ProfilePersistenceAssembler;
import axiom.daos.tourmate.platform.profiles.infrastructure.persistence.jpa.repositories.ProfilePersistenceRepository;
import axiom.daos.tourmate.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the domain {@link ProfileRepository} port with Spring Data JPA.
 */
@Repository
public class ProfileRepositoryImpl implements ProfileRepository {

    private final ProfilePersistenceRepository profilePersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public ProfileRepositoryImpl(
            ProfilePersistenceRepository profilePersistenceRepository,
            DomainEventPublisher domainEventPublisher
    ) {
        this.profilePersistenceRepository = profilePersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Profile save(Profile profile) {
        var saved = profilePersistenceRepository.save(ProfilePersistenceAssembler.toPersistenceFromDomain(profile));
        var savedProfile = ProfilePersistenceAssembler.toDomainFromPersistence(saved);
        domainEventPublisher.publishAndClearEvents(profile);
        return savedProfile;
    }

    @Override
    public Optional<Profile> findById(Long id) {
        return profilePersistenceRepository.findById(id)
                .map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Profile> findByEmail(EmailAddress email) {
        return profilePersistenceRepository.findByEmail(email)
                .map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Profile> findByUserId(UserId userId) {
        return profilePersistenceRepository.findByUserId(userId)
                .map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Profile> findAll() {
        return profilePersistenceRepository.findAll().stream()
                .map(ProfilePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return profilePersistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return profilePersistenceRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndIdIsNot(EmailAddress email, Long id) {
        return profilePersistenceRepository.existsByEmailAndIdIsNot(email, id);
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        return profilePersistenceRepository.existsByUserId(userId);
    }

    @Override
    public void deleteById(Long id) {
        profilePersistenceRepository.deleteById(id);
    }
}
