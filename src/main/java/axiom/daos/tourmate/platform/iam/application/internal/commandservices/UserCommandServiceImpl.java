package axiom.daos.tourmate.platform.iam.application.internal.commandservices;

import axiom.daos.tourmate.platform.iam.application.commandservices.UserCommandService;
import axiom.daos.tourmate.platform.iam.application.internal.outboundservices.hashing.HashingService;
import axiom.daos.tourmate.platform.iam.application.internal.outboundservices.tokens.TokenService;
import axiom.daos.tourmate.platform.iam.domain.model.aggregates.User;
import axiom.daos.tourmate.platform.iam.domain.model.commands.ResetAllUsersPasswordCommand;
import axiom.daos.tourmate.platform.iam.domain.model.commands.ResetUserPasswordCommand;
import axiom.daos.tourmate.platform.iam.domain.model.commands.SignInCommand;
import axiom.daos.tourmate.platform.iam.domain.model.commands.SignUpCommand;
import axiom.daos.tourmate.platform.iam.domain.repositories.RoleRepository;
import axiom.daos.tourmate.platform.iam.domain.repositories.UserRepository;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * User command service implementation.
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;

    public UserCommandServiceImpl(
            UserRepository userRepository,
            HashingService hashingService,
            TokenService tokenService,
            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
    }

    @Override
    public Result<ImmutablePair<User, String>, ApplicationError> handle(SignInCommand command) {
        var user = userRepository.findByUsername(command.username());
        if (user.isEmpty()) {
            return Result.failure(ApplicationError.notFound("User", command.username()));
        }
        if (!hashingService.matches(command.password(), user.get().getPassword())) {
            return Result.failure(ApplicationError.validationError("credentials", "Invalid username or password"));
        }
        var token = tokenService.generateToken(user.get().getUsername());
        return Result.success(ImmutablePair.of(user.get(), token));
    }

    @Override
    public Result<User, ApplicationError> handle(SignUpCommand command) {
        if (userRepository.existsByUsername(command.username())) {
            return Result.failure(ApplicationError.conflict("User", "Username already exists"));
        }
        var roles = command.roles().stream()
                .map(role -> roleRepository.findByName(role.getName()))
                .toList();

        if (roles.stream().anyMatch(java.util.Optional::isEmpty)) {
            return Result.failure(ApplicationError.notFound("Role", "one or more role names"));
        }

        var resolvedRoles = roles.stream()
                .map(java.util.Optional::get)
                .toList();

        var user = new User(command.username(), hashingService.encode(command.password()), resolvedRoles);
        userRepository.save(user);
        return userRepository.findByUsername(command.username())
                .<Result<User, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.unexpected("sign-up", "Created user could not be reloaded")));
    }

    @Override
    @Transactional
    public Result<User, ApplicationError> handle(ResetUserPasswordCommand command) {
        if (command.newPassword() == null || command.newPassword().trim().length() < 8) {
            return Result.failure(ApplicationError.validationError(
                    "password",
                    "New password must contain at least 8 characters"
            ));
        }

        Optional<User> existing = Optional.empty();
        if (command.userId() != null && command.userId() > 0) {
            existing = userRepository.findById(command.userId());
        } else if (command.username() != null && !command.username().isBlank()) {
            existing = userRepository.findByUsername(command.username().trim());
        } else {
            return Result.failure(ApplicationError.validationError(
                    "user",
                    "Either userId or username is required to reset password"
            ));
        }

        if (existing.isEmpty()) {
            String identifier = command.userId() != null ? String.valueOf(command.userId()) : command.username();
            return Result.failure(ApplicationError.notFound("User", identifier));
        }

        var user = existing.get();
        user.updatePassword(hashingService.encode(command.newPassword()));
        return Result.success(userRepository.save(user));
    }

    @Override
    @Transactional
    public Result<Integer, ApplicationError> handle(ResetAllUsersPasswordCommand command) {
        String targetPassword = (command.newPassword() == null || command.newPassword().isBlank())
                ? "12345678"
                : command.newPassword();

        if (targetPassword.trim().length() < 8) {
            return Result.failure(ApplicationError.validationError(
                    "password",
                    "New password must contain at least 8 characters"
            ));
        }

        var users = userRepository.findAll();
        String encoded = hashingService.encode(targetPassword);
        int updatedCount = 0;
        for (var user : users) {
            user.updatePassword(encoded);
            userRepository.save(user);
            updatedCount++;
        }
        return Result.success(updatedCount);
    }
}
