package axiom.daos.tourmate.platform.tourmonitoring.domain.model.events;

public record ActiveTourCreatedEvent(
        Long activeTourId,
        Long tourScheduleId,
        Long tourGuideId,
        double currentLatitude,
        double currentLongitude,
        String startedAt,
        String finishedAt
) {

    public static ActiveTourCreatedEvent from(Long activeTourId, Long tourScheduleId, Long tourGuideId, double currentLatitude, double currentLongitude, String startedAt, String finishedAt) {

        return new ActiveTourCreatedEvent(
                activeTourId,
                tourScheduleId,
                tourGuideId, currentLatitude, currentLongitude, startedAt, finishedAt);
    }
}
