package axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(

        name = "UpdateActiveTourRequest",
        description = "Request to update an existing active tour",
        example = "{\"tourScheduleId\": 1, \"tourGuideId\": 1, \"currentLatitude\": 40.7128, \"currentLongitude\": -74.0060, \"startedAt\": \"2023-10-01T10:00:00Z\", \"finishedAt\": \"2023-10-01T12:00:00Z\", \"status\": \"IN_PROGRESS\"}")
public record UpdateActiveTourResource(
        @Schema(description = "Tour schedule unique identifier", example = "1")
        Long tourScheduleId,
        @Schema(description = "Tour guide unique identifier", example = "1")
        Long tourGuideId,
        @Schema(description = "Status of the active tour", example = "IN_PROGRESS")
        String status,
        @Schema(description = "Current latitude of the active tour", example = "40.7128")
        double currentLatitude,
        @Schema(description = "Current longitude of the active tour", example = "-74.0060")
        double currentLongitude,
        @Schema(description = "Timestamp when the active tour started", example = "2023-10-01T10:00:00Z")
        String startedAt,
        @Schema(description = "Timestamp when the active tour finished", example = "2023-10-01T12:00:00Z")
        String finishedAt
) {

    public UpdateActiveTourResource{
        if(tourScheduleId == null){
            throw new IllegalArgumentException("Tour schedule ID is required");
        }
        if(tourGuideId == null){
            throw new IllegalArgumentException("Tour guide ID is required");
        }
        if(currentLatitude < -90 || currentLatitude > 90){
            throw new IllegalArgumentException("Current latitude is out of bounds");
        }
        if(currentLongitude < -180 || currentLongitude > 180){
            throw new IllegalArgumentException("Current longitude is out of bounds");
        }
        if(startedAt == null || startedAt.isBlank()){
            throw new IllegalArgumentException("Started at timestamp is required");
        }

    }
}
