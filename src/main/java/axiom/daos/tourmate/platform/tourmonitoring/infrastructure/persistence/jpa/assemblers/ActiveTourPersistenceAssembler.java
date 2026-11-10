package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.ActiveTour;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.entities.ActiveTourPersistenceEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ActiveTourPersistenceAssembler {
    private ActiveTourPersistenceAssembler() {
    }

    public static ActiveTour toDomainFromPersistence(ActiveTourPersistenceEntity entity) {
        if (entity == null) return null;


        var activeTour = ActiveTour.reconstitute(
                entity.getId(),
                entity.getTourScheduleId(),
                entity.getTourGuideId(),
                entity.getStatus(),
                entity.getCurrentLatitude(),
                entity.getCurrentLongitude(),
                entity.getStartedAt(),
                entity.getFinishedAt());

        return activeTour;
    }

    public static ActiveTourPersistenceEntity toPersistenceFromDomain(ActiveTour activeTour) {
        if (activeTour == null) return null;

        var entity = new ActiveTourPersistenceEntity();
        // Only set ID if the activeTour is being updated (has a non-null ID)
        // For new activeTours, leave ID null to allow JPA to generate it
        if (activeTour.getId() != null) {
            entity.setId(activeTour.getId());
        }
        entity.setTourScheduleId(activeTour.getTourScheduleId());
        entity.setTourGuideId(activeTour.getTourGuideId());
        entity.setStatus(activeTour.getStatus());
        entity.setCurrentLatitude(activeTour.getCurrentLatitude());
        entity.setCurrentLongitude(activeTour.getCurrentLongitude());
        entity.setStartedAt(activeTour.getStartedAt());
        entity.setFinishedAt(activeTour.getFinishedAt());

        return entity;
    }
}
