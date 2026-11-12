package axiom.daos.tourmate.platform.tour_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Coordinates;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.AddCheckpointToTourCommand;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.AddCheckpointResource;

public class AddCheckpointToTourCommandFromResourceAssembler {

    public static AddCheckpointToTourCommand toCommandFromResource(Long tourId,AddCheckpointResource resource) {

        return new AddCheckpointToTourCommand(
                tourId,
                resource.name(),
                new Coordinates(resource.x(), resource.y()),
                resource.order(),
                resource.description()
        );
    }
}
