package axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands;

public record CreateParticipantCommand(
        Long tourScheduleId,
        Long profileId,
        String joinedAt
) {
    public CreateParticipantCommand{
        if (tourScheduleId == null || tourScheduleId <= 0) {
            throw new IllegalArgumentException("tourScheduleId cannot be null or less than 1");
        }
        if (profileId == null || profileId <= 0) {
            throw new IllegalArgumentException("profileId cannot be null or less than 1");
        }
    }
}
