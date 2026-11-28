package axiom.daos.tourmate.platform.profiles.application.internal.commandservices;

import axiom.daos.tourmate.platform.profiles.application.commandservices.ProfileCommandService;
import axiom.daos.tourmate.platform.profiles.application.internal.outboundservices.acl.ExternalIamService;
import axiom.daos.tourmate.platform.profiles.domain.model.aggregates.Profile;
import axiom.daos.tourmate.platform.profiles.domain.model.commands.CreateProfileCommand;
import axiom.daos.tourmate.platform.profiles.domain.model.commands.DeleteProfileCommand;
import axiom.daos.tourmate.platform.profiles.domain.model.commands.UpdateProfileCommand;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.EmailAddress;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.UserId;
import axiom.daos.tourmate.platform.profiles.domain.repositories.ProfileRepository;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ProfileCommandService}.
 */
@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final ProfileRepository profileRepository;
    private final ExternalIamService externalIamService;

    public ProfileCommandServiceImpl(
            ProfileRepository profileRepository,
            ExternalIamService externalIamService
    ) {
        this.profileRepository = profileRepository;
        this.externalIamService = externalIamService;
    }

    @Override
    public Result<Profile, ApplicationError> handle(CreateProfileCommand command) {
        try {
            var emailAddress = new EmailAddress(command.email());
            var userId = new UserId(command.userId());

            if (!externalIamService.existsUserById(command.userId())) {
                return Result.failure(ApplicationError.notFound("User", command.userId().toString()));
            }

            if (profileRepository.existsByUserId(userId)) {
                return Result.failure(ApplicationError.conflict(
                        "Profile",
                        "A profile already exists for user with ID %d".formatted(command.userId())
                ));
            }

            if (profileRepository.existsByEmail(emailAddress)) {
                return Result.failure(ApplicationError.conflict(
                        "Profile",
                        "Profile with email %s already exists".formatted(emailAddress.address())
                ));
            }

            var profile = new Profile(command);
            var savedProfile = profileRepository.save(profile);
            return Result.success(savedProfile);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Profile", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-profile", e.getMessage()));
        }
    }

    @Override
    public Result<Profile, ApplicationError> handle(UpdateProfileCommand command) {
        var existingProfile = profileRepository.findById(command.profileId());
        if (existingProfile.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Profile", command.profileId().toString()));
        }

        try {
            var emailAddress = new EmailAddress(command.email());

            if (profileRepository.existsByEmailAndIdIsNot(emailAddress, command.profileId())) {
                return Result.failure(ApplicationError.conflict(
                        "Profile",
                        "Profile with email %s already exists".formatted(emailAddress.address())
                ));
            }

            var profileToUpdate = existingProfile.get();
            var updatedProfile = profileRepository.save(
                    profileToUpdate.updateInformation(
                            command.firstName(),
                            command.lastName(),
                            command.email()
                    )
            );
            return Result.success(updatedProfile);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Profile", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("update-profile", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(DeleteProfileCommand command) {
        if (!profileRepository.existsById(command.profileId())) {
            return Result.failure(ApplicationError.notFound("Profile", command.profileId().toString()));
        }

        try {
            profileRepository.deleteById(command.profileId());
            return Result.success(command.profileId());
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("delete-profile", e.getMessage()));
        }
    }
}
