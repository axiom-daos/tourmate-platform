package axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.transform;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.ActiveTour;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources.ActiveTourResource;

public class ActiveTourResourceFromEntityAssembler {
    public static ActiveTourResource toResourceFromEntity(ActiveTour entity) {
        return new ActiveTourResource(
                entity.getId(),
                entity.getTourScheduleId().tourScheduleId(),
                entity.getTourGuideId().tourGuideId(),
                entity.getCurrentLatitude(),
                entity.getCurrentLongitude(),
                entity.getStartedAt(),
                entity.getFinishedAt());
    }
}
