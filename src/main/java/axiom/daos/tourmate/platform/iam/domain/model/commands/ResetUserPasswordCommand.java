package axiom.daos.tourmate.platform.iam.domain.model.commands;

/**
 * Command to reset the password of a specific user identified by {@code userId} or {@code username}.
 */
public record ResetUserPasswordCommand(
        Long userId,
        String username,
        String newPassword
) {
}
