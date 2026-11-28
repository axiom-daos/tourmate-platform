package axiom.daos.tourmate.platform.profiles.domain.model.commands;

/**
 * Command to create a new profile linked to an IAM user.
 *
 * @param firstName The person's first name.
 * @param lastName  The person's last name.
 * @param email     The person's email address.
 * @param userId    The identifier of the associated IAM user.
 */
public record CreateProfileCommand(
        String firstName,
        String lastName,
        String email,
        Long userId
) {
    public CreateProfileCommand {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (userId == null || userId < 1) {
            throw new IllegalArgumentException("User id cannot be null or less than 1");
        }
    }
}
