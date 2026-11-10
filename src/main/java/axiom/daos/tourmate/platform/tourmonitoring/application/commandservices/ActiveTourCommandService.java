package axiom.daos.tourmate.platform.tourmonitoring.application.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.ActiveTour;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.CreateActiveTourCommand;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.DeleteActiveTourCommand;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.UpdateActiveTourCommand;

public interface ActiveTourCommandService {
    /**
     * Handle Create ActiveTour Command
     *
     * @param command The {@link CreateActiveTourCommand} Command
     * @return A {@link Result} containing the ID of the created {@link ActiveTour} on success,
     *         or an {@link ApplicationError} on failure (validation or business rule violation)
     */
    Result<Long, ApplicationError> handle(CreateActiveTourCommand command);

    Result<ActiveTour, ApplicationError> handle(UpdateActiveTourCommand command);

    Result<Long, ApplicationError> handle(DeleteActiveTourCommand command);
}
