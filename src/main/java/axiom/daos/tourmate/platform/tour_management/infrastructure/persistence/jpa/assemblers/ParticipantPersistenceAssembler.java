package axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.tour_management.domain.model.entities.Participant;
import axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.entities.ParticipantPersistenceEntity;

public class ParticipantPersistenceAssembler {

    private ParticipantPersistenceAssembler(){}

    public static Participant toDomainFromPersistence(ParticipantPersistenceEntity entity) {
        if(entity == null) return null;

        return Participant.reconstitute(entity.getUserId(), entity.getJoinedAt());
    }

    public static ParticipantPersistenceEntity toPersistenceFromDomain(Participant participant) {
        if(participant == null) return null;

        var entity = new ParticipantPersistenceEntity();

        if(participant.getId() != null) {
            entity.setUserId(participant.getId());
        }

        entity.setJoinedAt(participant.getJoinedAt());

        return entity;
    }
}
