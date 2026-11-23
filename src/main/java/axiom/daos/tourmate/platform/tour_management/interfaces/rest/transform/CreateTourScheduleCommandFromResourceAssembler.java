package axiom.daos.tourmate.platform.tour_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.tour_management.domain.model.commands.CreateTourScheduleCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.DepartureDateTime;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourId;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.CreateTourScheduleResource;

public class CreateTourScheduleCommandFromResourceAssembler {

    public static CreateTourScheduleCommand toCommandFromResource(CreateTourScheduleResource resource) {
        return new CreateTourScheduleCommand(
                new TourId(resource.tourId()),
                new DepartureDateTime(resource.departureDateTime()),
                resource.maxCapacity()
        );
    }
}
