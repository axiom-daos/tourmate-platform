package axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources;

import axiom.daos.tourmate.platform.shared.interfaces.rest.resources.CoordinatesResource;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "CheckpointResource",
        description = "Checkpoint information response"
)
public record CheckpointResource(
        @Schema(description = "Checkpoint unique identifier")
        Long id,

        @Schema(description = "Checkpoint name")
        String name,

       @Schema(description = "Checkpoint coordinates")
        CoordinatesResource coordinates,

        @Schema(description = "Order in the Tour checkpoint list")
        int order,

        @Schema(description = "Checkpoint description")
        String description
) {
}
