package axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources;

import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.AgencyId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        name = "CreateTourResource",
        description = "Request payload for creating a new tour"
)
public record CreateTourResource(
        @NotNull(message = "agency ID is required")
        @Schema(description = "Agency identifier")
        Long agencyID,

        @NotNull(message = "Tour details are required")
        @Schema(description = "Tour details")
        CreateTourDetailsResource details
) {
}
