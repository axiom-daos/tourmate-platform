package axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "ParticipantResource",
        description = "Participant information resource"
)
public record ParticipantResource(

        @Schema(description = "User identifier")
        Long userId,

        @Schema(description = "Participant joined at")
        LocalDateTime joinedAt
) {
}
