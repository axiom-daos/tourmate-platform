package axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.transform;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.UpdateActiveTourCommand;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources.UpdateActiveTourResource;

public class UpdateActiveTourCommandFromResourceAssembler {
    /**
     * Converts a UpdateActiveTourResource to a UpdateActiveTourCommand.
     *
     * @param activeTourId The activeTour ID.
     * @param resource The {@link UpdateActiveTourResource} resource to convert.
     * @return The {@link UpdateActiveTourCommand} command that results from the conversion.
     */
    public static UpdateActiveTourCommand toCommandFromResource(Long activeTourId, UpdateActiveTourResource resource) {
        return new UpdateActiveTourCommand(
                activeTourId,
                resource.tourScheduleId(),
                resource.tourGuideId(),
                resource.status(),
                resource.currentLatitude(),
                resource.currentLongitude(),
                resource.startedAt(),
                resource.finishedAt()
                );
    }
}
