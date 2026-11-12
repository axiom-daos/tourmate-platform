package axiom.daos.tourmate.platform.tour_management.application.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.Tour;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.AddCheckpointToTourCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.CreateTourCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.DeleteTourCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.UpdateTourCommand;

public interface TourCommandService {

    Result<Tour, ApplicationError> handle(CreateTourCommand command);

    Result<Tour, ApplicationError> handle(UpdateTourCommand command);

    Result<Long, ApplicationError> handle(DeleteTourCommand command);

    Result<Tour, ApplicationError> handle(AddCheckpointToTourCommand command);
}
