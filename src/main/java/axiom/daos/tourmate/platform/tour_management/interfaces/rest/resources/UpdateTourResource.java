package axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        name = "UpdateTourResource",
        description = "Request payload for updating a existing tour"
)
public record UpdateTourResource(

        @NotNull(message = "Tour details are required")
        @Schema(description = "Tour details")
        CreateTourDetailsResource details
) {
}
