package axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.transform;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.CreateActiveTourCommand;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources.CreateActiveTourResource;

public class CreateActiveTourCommandFromResourceAssembler {
    /**
     * Converts a CreateActiveTourResource to a CreateActiveTourCommand.
     *
     * @param resource The {@link CreateActiveTourResource} resource to convert.
     * @return The {@link CreateActiveTourCommand} command that results from the conversion.
     */
    public static CreateActiveTourCommand toCommandFromResource(CreateActiveTourResource resource) {
        return new CreateActiveTourCommand(
                resource.tourScheduleId(),
                resource.tourGuideId(),
                resource.currentLatitude(),
                resource.currentLongitude(),
                resource.startedAt(),
                resource.finishedAt());
    }
}
