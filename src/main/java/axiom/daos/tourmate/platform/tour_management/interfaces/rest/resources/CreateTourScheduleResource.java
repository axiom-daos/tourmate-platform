package axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(
        name = "CreateTourScheduleResource",
        description = "Request payload for creating a new tour schedule"
)
public record CreateTourScheduleResource(
        @NotNull
        @Schema(description = "Tour identifier")
        Long tourId,

        @NotNull
        @Schema(description = "Departure datetime")
        LocalDateTime departureDateTime,

        @NotNull
        @Schema(description = "Max capacity")
        int maxCapacity
) {

}
