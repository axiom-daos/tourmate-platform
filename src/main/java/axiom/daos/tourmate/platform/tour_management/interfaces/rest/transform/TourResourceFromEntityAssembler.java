package axiom.daos.tourmate.platform.tour_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.shared.interfaces.rest.resources.CoordinatesResource;
import axiom.daos.tourmate.platform.shared.interfaces.rest.resources.MoneyResource;
import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.Tour;
import axiom.daos.tourmate.platform.tour_management.domain.model.entities.Checkpoint;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourDetails;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.CheckpointResource;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.TourDetailsResource;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.TourResource;

public class TourResourceFromEntityAssembler {

    public static TourResource toResourceFromEntity(Tour entity) {
        return new TourResource(
                entity.getId(),
                entity.getAgencyId().value(),
                entity.getStatus().name(),
                toResourceFrom(entity.getTourDetails()),
                entity.getCheckpoints().stream().map(TourResourceFromEntityAssembler::toResourceFrom).toList()
                );
    }

    private static TourDetailsResource toResourceFrom(TourDetails tourDetails) {
        return new TourDetailsResource(
                tourDetails.title(),
                tourDetails.description(),
                tourDetails.duration(),
                tourDetails.difficultyLevel().name(),
                new MoneyResource(tourDetails.price().amount(), tourDetails.price().currency().code()));
    }

    private static CheckpointResource toResourceFrom(Checkpoint checkpoint) {
        CoordinatesResource coordinatesResource = new CoordinatesResource(
                checkpoint.getLocation().x(),
                checkpoint.getLocation().y()
        );

        return new CheckpointResource(
                checkpoint.getId(),
                checkpoint.getName(),
                coordinatesResource,
                checkpoint.getOrder(),
                checkpoint.getDescription()
        );
    }
}
