package axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.transform;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.Participant;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources.ParticipantResource;

public class ParticipantResourceFromEntityAssembler {
    public static ParticipantResource toResourceFromEntity(Participant entity) {
        return new ParticipantResource(
                entity.getId(),
                entity.getTourScheduleId().tourScheduleId(),
                entity.getProfileId().profileId(),
                entity.getJoinedAt());
    }
}
