package axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        name = "AddCheckpointResource",
        description = "Request payload for add a checkpoint in a existing tour"
)
public record AddCheckpointResource(

        @NotNull
        String name,
        @NotNull
        Double x,

        @NotNull
        Double y,

        @NotNull
        int order,

        @NotNull
        String description
) {
}
