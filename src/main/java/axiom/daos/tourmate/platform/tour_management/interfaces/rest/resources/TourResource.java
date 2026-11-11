package axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
        name = "TourResource",
        description = "Tour information response"
)
public record TourResource(
        @Schema(description = "Tour unique identifier")
        Long id,

        @Schema(description = "Agency identifier")
        Long agencyId,

        @Schema(description = "Tour status")
        String status,

        @Schema(description = "Tour details")
        TourDetailsResource details,

        @Schema(description = "Tour list checkpoints")
        List<CheckpointResource> checkpoints
) {
}
