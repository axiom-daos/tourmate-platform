package axiom.daos.tourmate.platform.tour_management.application.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.TourSchedule;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.CloseTourScheduleCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.CreateTourScheduleCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.DeleteTourScheduleCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.OpenTourScheduleCommand;

public interface TourScheduleCommandService {

    Result<TourSchedule, ApplicationError> handle(CreateTourScheduleCommand command);

    Result<Long, ApplicationError> handle(DeleteTourScheduleCommand command);

    Result<TourSchedule, ApplicationError> handle(OpenTourScheduleCommand command);

    Result<TourSchedule, ApplicationError> handle(CloseTourScheduleCommand command);
}
