package axiom.daos.tourmate.platform.tour_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.TourSchedule;
import axiom.daos.tourmate.platform.tour_management.domain.model.entities.Participant;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.ParticipantResource;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.TourScheduleResource;

public class TourScheduleResourceFromEntityAssembler {

    public static TourScheduleResource toResourceFromEntity(TourSchedule entity) {
        return new TourScheduleResource(
                entity.getId(),
                entity.getStatus().name(),
                entity.getTourId().value(),
                entity.getDepartureDateTime().value(),
                entity.getMaxCapacity(),
                entity.getParticipants().stream().map(TourScheduleResourceFromEntityAssembler::toResourceFrom).toList()
        );
    }

    private static ParticipantResource toResourceFrom(Participant entity) {
        return new ParticipantResource(
                entity.getId().value(),
                entity.getJoinedAt()
        );
    }
}
