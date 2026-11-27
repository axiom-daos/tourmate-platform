package axiom.daos.tourmate.platform.tour_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.tour_management.domain.model.commands.UpdateTourSheduleCommand;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.UpdateTourScheduleResource;

public final class UpdateTourScheduleCommandFromResourceAssembler {

    public static UpdateTourSheduleCommand toCommandFromResource(Long tourScheduleId,UpdateTourScheduleResource resource) {
        return new UpdateTourSheduleCommand(
                tourScheduleId,
                resource.departureDateTime(),
                resource.maxCapacity()
        );
    }
}
