package axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.Tour;
import axiom.daos.tourmate.platform.tour_management.domain.model.entities.Checkpoint;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.AgencyId;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourStatus;
import axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.entities.TourPersistenceEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TourPersistenceAssembler {

    private TourPersistenceAssembler() {}

    public static Tour toDomainFromPersistence(TourPersistenceEntity entity) {
        if(entity == null) return null;

        List<Checkpoint> checkpoints = entity.getCheckpoints().stream().map(CheckpointPersistenceAssembler::toDomainFromPersistence).toList();

        return Tour.reconstitute(
                entity.getId(),
                new AgencyId(entity.getAgencyId()),
                TourDetailsPersistenceAssembler.toDomainFromPersistence(entity.getTourDetails()),
                checkpoints,
                entity.getStatus());
    }

    public static TourPersistenceEntity toPersistenceFromDomain(Tour tour) {
        if(tour == null) return null;

        var entity = new TourPersistenceEntity();

        entity.setId(tour.getId());
        entity.setAgencyId(tour.getAgencyId().value());
        entity.setTourDetails(TourDetailsPersistenceAssembler.toPersistenceFromDomain(tour.getTourDetails()));
        entity.setCheckpoints(
                tour.getCheckpoints().stream()
                        .map(CheckpointPersistenceAssembler::toPersistenceFromDomain).collect(Collectors.toCollection(ArrayList::new))
        );
        entity.setStatus(tour.getStatus());

        return entity;
    }
}
