package axiom.daos.tourmate.platform.iam.infrastructure.persistence.jpa.adapters;

import axiom.daos.tourmate.platform.iam.domain.model.aggregates.User;
import axiom.daos.tourmate.platform.iam.domain.repositories.UserRepository;
import axiom.daos.tourmate.platform.iam.infrastructure.persistence.jpa.assemblers.UserPersistenceAssembler;
import axiom.daos.tourmate.platform.iam.infrastructure.persistence.jpa.repositories.UserPersistenceRepository;
import axiom.daos.tourmate.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the IAM user domain repository port with Spring Data JPA.
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserPersistenceRepository userPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public UserRepositoryImpl(
            UserPersistenceRepository userPersistenceRepository,
            DomainEventPublisher domainEventPublisher) {
        this.userPersistenceRepository = userPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userPersistenceRepository.findById(id).map(UserPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userPersistenceRepository.findByUsername(username).map(UserPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<User> findAll() {
        return userPersistenceRepository.findAll().stream().map(UserPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public User save(User user) {
        var saved = userPersistenceRepository.save(UserPersistenceAssembler.toPersistenceFromDomain(user));
        var savedUser = UserPersistenceAssembler.toDomainFromPersistence(saved);
        domainEventPublisher.publishAndClearEvents(user);
        return savedUser;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userPersistenceRepository.existsByUsername(username);
    }
}

