package axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Participant resource.
 */
@Schema(
        name = "ParticipantResponse",
        description = "Participant information response",
        example = "{\"id\": 1, \"tourScheduleId\": 1, \"profileId\": 1, \"joinedAt\": \"2023-10-01T12:00:00Z\"}")
public record ParticipantResource(
        @Schema(description = "Participant unique identifier", example = "1")
        Long id,
        @Schema(description = "Tour schedule unique identifier", example = "1")
        Long tourScheduleId,
        @Schema(description = "Tour guide unique identifier", example = "1")
        Long profileId,
        @Schema(description = "Timestamp when the active tour joined", example = "2023-10-01T12:00:00Z")
        String joinedAt
) {
}