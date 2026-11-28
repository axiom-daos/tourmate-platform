package axiom.daos.tourmate.platform.iam.domain.model.commands;

/**
 * Command to reset the password of all existing users in the system.
 */
public record ResetAllUsersPasswordCommand(
        String newPassword
) {
}
