package axiom.daos.tourmate.platform.shared.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

public record CreateCoordinatesResource(
        @NotNull(message = "x axis is required")
        Double x,

        @NotNull(message = "y axis is required")
        Double y
) {
}
