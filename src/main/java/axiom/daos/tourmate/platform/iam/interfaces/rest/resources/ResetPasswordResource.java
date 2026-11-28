package axiom.daos.tourmate.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO resources for resetting a single user's password or all existing users' passwords.
 */
public final class ResetPasswordResource {

    private ResetPasswordResource() {
    }

    @Schema(
            name = "ResetUserPasswordRequest",
            description = "Payload to reset a user's password by username",
            example = "{\"username\": \"guide.cusco\", \"newPassword\": \"12345678\"}"
    )
    public record ResetByUsernameRequest(
            @NotBlank(message = "Username is required")
            String username,

            @NotBlank(message = "New password is required")
            @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
            String newPassword
    ) {
    }

    @Schema(
            name = "ResetUserPasswordByIdRequest",
            description = "Payload to reset a user's password by user ID",
            example = "{\"newPassword\": \"12345678\"}"
    )
    public record ResetByIdRequest(
            @NotBlank(message = "New password is required")
            @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
            String newPassword
    ) {
    }

    @Schema(
            name = "ResetAllUsersPasswordRequest",
            description = "Payload to reset the password of all existing users (defaults to 12345678 if omitted)",
            example = "{\"newPassword\": \"12345678\"}"
    )
    public record ResetAllRequest(
            String newPassword
    ) {
    }

    @Schema(name = "ResetAllUsersPasswordResponse")
    public record ResetAllResponse(
            int updatedUsersCount,
            String message
    ) {
    }
}
