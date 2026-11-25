package axiom.daos.tourmate.platform.tour_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.tour_management.domain.model.commands.AddParticipantCommand;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.AddParticipantResource;

public final class AddParticipantCommandFromResourceAssembler {

    public static AddParticipantCommand toCommandFromResource(Long tourScheduleId,AddParticipantResource resource) {
        return new AddParticipantCommand(tourScheduleId, resource.userId());
    }
}
