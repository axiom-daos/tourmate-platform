package axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Active tour resource.
 */
@Schema(
        name = "ActiveTourResponse",
        description = "Active tour information response",
        example = "{\"id\": 1, \"tourScheduleId\": 1, \"tourGuideId\": 1, \"currentLatitude\": 40.7128, \"currentLongitude\": -74.0060, \"startedAt\": \"2023-10-01T10:00:00Z\", \"finishedAt\": \"2023-10-01T12:00:00Z\"}")
public record ActiveTourResource(
        @Schema(description = "Active tour unique identifier", example = "1")
        Long id,

        @Schema(description = "Tour schedule unique identifier", example = "1")
        Long tourScheduleId,
        @Schema(description = "Tour guide unique identifier", example = "1")
        Long tourGuideId,
        @Schema(description = "Current latitude of the active tour", example = "40.7128")
        double currentLatitude,
        @Schema(description = "Current longitude of the active tour", example = "-74.0060")
        double currentLongitude,
        @Schema(description = "Timestamp when the active tour started", example = "2023-10-01T10:00:00Z")
        String startedAt,
        @Schema(description = "Timestamp when the active tour finished", example = "2023-10-01T12:00:00Z")
        String finishedAt
) {
}
