package axiom.daos.tourmate.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request resource for updating an existing profile.
 */
@Schema(
        name = "UpdateProfileRequest",
        description = "Request payload to update an existing profile's personal information",
        example = "{\"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\"}"
)
public record UpdateProfileResource(
        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Updated first name", example = "John", maxLength = 100)
        String firstName,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Updated last name", example = "Doe", maxLength = 100)
        String lastName,

        @NotBlank(message = "{validation.not-blank}")
        @Email(message = "{validation.email}")
        @Schema(description = "Updated email address", example = "john.doe@example.com", maxLength = 150)
        String email
) {
}
