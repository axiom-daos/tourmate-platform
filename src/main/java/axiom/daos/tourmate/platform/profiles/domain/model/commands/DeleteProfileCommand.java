package axiom.daos.tourmate.platform.profiles.domain.model.commands;

/**
 * Command to delete an existing profile by identifier.
 *
 * @param profileId The identifier of the profile to delete.
 */
public record DeleteProfileCommand(Long profileId) {
    public DeleteProfileCommand {
        if (profileId == null || profileId < 1) {
            throw new IllegalArgumentException("Profile id cannot be null or less than 1");
        }
    }
}
