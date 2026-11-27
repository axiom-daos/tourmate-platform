package axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "CreateParticipantRequest",
        description = "Request to create a new participant",
        example = "{\"tourScheduleId\": 1, \"profileId\": 1, \"joinedAt\": \"2023-10-01T12:00:00Z\"}")
public record CreateParticipantResource(
        @Schema(description = "Tour schedule unique identifier", example = "1")
        Long tourScheduleId,
        @Schema(description = "Tour guide unique identifier", example = "1")
        Long profileId,
        @Schema(description = "Timestamp when the participant joined; may be null or blank while the tour is in progress", example = "2023-10-01T12:00:00Z", nullable = true)
        String joinedAt
) {

    public CreateParticipantResource{
        if(tourScheduleId == null){
            throw new IllegalArgumentException("Tour schedule ID is required");
        }
        if(profileId == null){
            throw new IllegalArgumentException("Tour guide ID is required");
        }

    }
}
