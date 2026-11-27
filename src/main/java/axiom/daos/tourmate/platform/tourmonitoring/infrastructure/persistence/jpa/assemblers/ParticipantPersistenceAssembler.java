package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.Participant;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.entities.ParticipantPersistenceEntity;

public class ParticipantPersistenceAssembler {
    private ParticipantPersistenceAssembler() {
    }

    public static Participant toDomainFromPersistence(ParticipantPersistenceEntity entity) {
        if (entity == null) return null;


        var participant = Participant.reconstitute(
                entity.getId(),
                entity.getTourScheduleId(),
                entity.getProfileId(),
                entity.getJoinedAt());

        return participant;
    }

    public static ParticipantPersistenceEntity toPersistenceFromDomain(Participant participant) {
        if (participant == null) return null;

        var entity = new ParticipantPersistenceEntity();
        // Only set ID if the participant is being updated (has a non-null ID)
        // For new participants, leave ID null to allow JPA to generate it
        if (participant.getId() != null) {
            entity.setId(participant.getId());
        }
        entity.setTourScheduleId(participant.getTourScheduleId());
        entity.setProfileId(participant.getProfileId());
        entity.setJoinedAt(participant.getJoinedAt());

        return entity;
    }
}
