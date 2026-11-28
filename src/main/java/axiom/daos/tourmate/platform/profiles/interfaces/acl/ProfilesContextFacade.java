package axiom.daos.tourmate.platform.profiles.interfaces.acl;

import axiom.daos.tourmate.platform.profiles.application.commandservices.ProfileCommandService;
import axiom.daos.tourmate.platform.profiles.application.queryservices.ProfileQueryService;
import axiom.daos.tourmate.platform.profiles.domain.model.commands.CreateProfileCommand;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetProfileByEmailQuery;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetProfileByIdQuery;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.EmailAddress;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.UserId;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

/**
 * ACL facade that exposes Profiles bounded context capabilities to other bounded contexts.
 */
@Service
public class ProfilesContextFacade {

    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;

    public ProfilesContextFacade(
            ProfileCommandService profileCommandService,
            ProfileQueryService profileQueryService
    ) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
    }

    /**
     * Creates a new profile linked to an existing IAM user.
     *
     * @param firstName The person's first name.
     * @param lastName  The person's last name.
     * @param email     The person's email address.
     * @param userId    The IAM user identifier.
     * @return The created profile identifier, or {@code 0L} when creation fails.
     */
    public Long createProfile(String firstName, String lastName, String email, Long userId) {
        try {
            var command = new CreateProfileCommand(firstName, lastName, email, userId);
            var result = profileCommandService.handle(command);
            if (result instanceof Result.Success(var profile) && profile.getId() != null) {
                return profile.getId();
            }
            return 0L;
        } catch (IllegalArgumentException e) {
            return 0L;
        }
    }

    /**
     * Fetches a profile identifier by email address.
     *
     * @param email The email address to search.
     * @return The profile identifier, or {@code 0L} when not found or invalid.
     */
    public Long fetchProfileIdByEmail(String email) {
        try {
            var query = new GetProfileByEmailQuery(new EmailAddress(email));
            var profile = profileQueryService.handle(query);
            return profile.map(p -> p.getId() != null ? p.getId() : 0L).orElse(0L);
        } catch (IllegalArgumentException e) {
            return 0L;
        }
    }

    /**
     * Fetches a profile identifier by associated IAM user identifier.
     *
     * @param userId The IAM user identifier.
     * @return The profile identifier, or {@code 0L} when not found or invalid.
     */
    public Long fetchProfileIdByUserId(Long userId) {
        try {
            var query = new GetProfileByUserIdQuery(new UserId(userId));
            var profile = profileQueryService.handle(query);
            return profile.map(p -> p.getId() != null ? p.getId() : 0L).orElse(0L);
        } catch (IllegalArgumentException e) {
            return 0L;
        }
    }

    /**
     * Checks whether a profile exists for the given profile identifier.
     *
     * @param profileId The profile identifier.
     * @return {@code true} if the profile exists; {@code false} otherwise.
     */
    public boolean existsProfileById(Long profileId) {
        if (profileId == null || profileId < 1) {
            return false;
        }
        return profileQueryService.handle(new GetProfileByIdQuery(profileId)).isPresent();
    }
}
