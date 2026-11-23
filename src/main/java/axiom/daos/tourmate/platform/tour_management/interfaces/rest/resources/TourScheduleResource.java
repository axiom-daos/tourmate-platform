package axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(
        name = "TourScheduleResource",
        description = "Tour schedule information response"
)
public record TourScheduleResource(

        @Schema(description = "Tour schedule unique identifier")
        Long id,

        @Schema(description = "Tour schedule status")
        String status,

        @Schema(description = "Tour identifier")
        Long tourId,

        @Schema(description = "Tour schedule departure datetime")
        LocalDateTime departureDateTime,

        @Schema(description = "Tour schedule max capacity")
        int maxCapacity,

        @Schema(description = "Tour schedule participants")
        List<ParticipantResource> participants

) {
}
