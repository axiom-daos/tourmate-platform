package axiom.daos.tourmate.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource representing a profile returned by the REST API.
 */
@Schema(
        name = "ProfileResponse",
        description = "Profile information response",
        example = "{\"id\": 1, \"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\", \"userId\": 1}"
)
public record ProfileResource(
        @Schema(description = "Profile unique identifier", example = "1")
        Long id,

        @Schema(description = "Person first name", example = "John")
        String firstName,

        @Schema(description = "Person last name", example = "Doe")
        String lastName,

        @Schema(description = "Person email address", example = "john.doe@example.com")
        String email,

        @Schema(description = "Associated IAM user identifier", example = "1")
        Long userId
) {
}
