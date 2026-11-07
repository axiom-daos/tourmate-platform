package axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.Status;
import org.apache.logging.log4j.util.Strings;

public record UpdateActiveTourCommand(
        Long activeTourId,
        Long tourScheduleId,
        Long tourGuideId,
        String status,
        double currentLatitude,
        double currentLongitude,
        String startedAt,
        String finishedAt
        ) {
    public UpdateActiveTourCommand{
        if (activeTourId == null || activeTourId <= 0) {
            throw new IllegalArgumentException("activeTourId cannot be null or less than 1");
        }
        if (tourScheduleId == null || tourScheduleId <= 0) {
            throw new IllegalArgumentException("tourScheduleId cannot be null or less than 1");
        }
        if (tourGuideId == null || tourGuideId <= 0) {
            throw new IllegalArgumentException("tourGuideId cannot be null or less than 1");
        }
        if(status == null || status.isEmpty() ) {
            throw new IllegalArgumentException("status cannot be null, empty or invalid");
        }
        if(currentLatitude < -90.0 || currentLatitude > 90.0) {
            throw new IllegalArgumentException("currentLatitude must be between -90 and 90");
        }
        if(currentLongitude < -180.0 || currentLongitude > 180.0) {
            throw new IllegalArgumentException("currentLongitude must be between -180 and 180");
        }
        if(startedAt == null || startedAt.isEmpty() ) {
            throw new IllegalArgumentException("startedAt cannot be null or empty");
        }

    }
}
