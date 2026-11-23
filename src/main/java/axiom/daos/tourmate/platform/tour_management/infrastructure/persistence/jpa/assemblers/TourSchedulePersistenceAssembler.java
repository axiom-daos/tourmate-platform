package axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.TourSchedule;
import axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.entities.TourSchedulePersistenceEntity;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TourSchedulePersistenceAssembler {

    private TourSchedulePersistenceAssembler(){}

    public static TourSchedule toDomainFromPersistence(TourSchedulePersistenceEntity entity) {
        if(entity == null) return null;

        var participants = entity.getParticipants().stream().map(ParticipantPersistenceAssembler::toDomainFromPersistence).toList();

        return TourSchedule.reconstitute(
                entity.getId(),
                entity.getTourId(),
                entity.getDepartureDateTime(),
                entity.getMaxCapacity(),
                participants,
                entity.getStatus()
        );

    }

    public static TourSchedulePersistenceEntity toPersistenceFromDomain(TourSchedule tourSchedule) {
        if(tourSchedule == null) return null;

        var entity = new TourSchedulePersistenceEntity();

        entity.setId(tourSchedule.getId());
        entity.setTourId(tourSchedule.getTourId());
        entity.setDepartureDateTime(tourSchedule.getDepartureDateTime());
        entity.setMaxCapacity(tourSchedule.getMaxCapacity());

        var participants = tourSchedule.getParticipants().stream()
                .map(ParticipantPersistenceAssembler::toPersistenceFromDomain)
                .collect(Collectors.toCollection(ArrayList::new));

        participants.forEach(p -> p.setTourSchedule(entity));

        entity.setParticipants(participants);
        entity.setStatus(tourSchedule.getStatus());

        return entity;
    }
}
