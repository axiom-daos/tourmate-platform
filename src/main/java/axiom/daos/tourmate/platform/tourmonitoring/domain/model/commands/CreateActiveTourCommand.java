package axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands;



public record CreateActiveTourCommand(Long tourScheduleId, Long tourGuideId) {

    public CreateActiveTourCommand{
        if (tourScheduleId == null || tourScheduleId <= 0) {
            throw new IllegalArgumentException("tourScheduleId cannot be null or less than 1");
        }
        if (tourGuideId == null || tourGuideId <= 0) {
            throw new IllegalArgumentException("tourGuideId cannot be null or less than 1");
        }
    }
}
