package axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.transform;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.CreateParticipantCommand;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources.CreateParticipantResource;

public class CreateParticipantCommandFromResourceAssembler {
    public static CreateParticipantCommand toCommandFromResource(CreateParticipantResource resource) {
        return new CreateParticipantCommand(
                resource.tourScheduleId(),
                resource.profileId(),
                resource.joinedAt());
    }
}
