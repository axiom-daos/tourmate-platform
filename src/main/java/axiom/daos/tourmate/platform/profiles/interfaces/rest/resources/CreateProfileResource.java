package axiom.daos.tourmate.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request resource for creating a new profile.
 */
@Schema(
        name = "CreateProfileRequest",
        description = "Request payload to create a new profile linked to an IAM user",
        example = "{\"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\", \"userId\": 1}"
)
public record CreateProfileResource(
        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Person first name", example = "John", maxLength = 100)
        String firstName,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Person last name", example = "Doe", maxLength = 100)
        String lastName,

        @NotBlank(message = "{validation.not-blank}")
        @Email(message = "{validation.email}")
        @Schema(description = "Person email address", example = "john.doe@example.com", maxLength = 150)
        String email,

        @NotNull(message = "User ID is required")
        @Positive(message = "User ID must be greater than 0")
        @Schema(description = "Associated IAM user identifier", example = "1")
        Long userId
) {
}
