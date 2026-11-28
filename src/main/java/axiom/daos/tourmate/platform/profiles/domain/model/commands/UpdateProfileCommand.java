package axiom.daos.tourmate.platform.profiles.domain.model.commands;

/**
 * Command to update an existing profile's personal information.
 *
 * @param profileId The identifier of the profile to update.
 * @param firstName The updated first name.
 * @param lastName  The updated last name.
 * @param email     The updated email address.
 */
public record UpdateProfileCommand(
        Long profileId,
        String firstName,
        String lastName,
        String email
) {
    public UpdateProfileCommand {
        if (profileId == null || profileId < 1) {
            throw new IllegalArgumentException("Profile id cannot be null or less than 1");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
    }
}
