package axiom.daos.tourmate.platform.tourmonitoring.application.internal.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.tourmonitoring.application.commandservices.ActiveTourCommandService;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.ActiveTour;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.CreateActiveTourCommand;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.DeleteActiveTourCommand;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.UpdateActiveTourCommand;
import axiom.daos.tourmate.platform.tourmonitoring.domain.repositories.ActiveTourRepository;
import org.springframework.stereotype.Service;

@Service
public class ActiveTourCommandServiceImpl implements ActiveTourCommandService {
    private final ActiveTourRepository activeTourRepository;

    public ActiveTourCommandServiceImpl(ActiveTourRepository activeTourRepository) {
        this.activeTourRepository = activeTourRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(CreateActiveTourCommand command) {

        var activeTour = new ActiveTour(command);
        try {
            activeTour = activeTourRepository.save(activeTour);
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-active-tour", e.getMessage()));
        }
        return Result.success(activeTour.getId());
    }

    @Override
    public Result<ActiveTour, ApplicationError> handle(UpdateActiveTourCommand command) {

        var result = activeTourRepository.findById(command.activeTourId());
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("ActiveTour", command.activeTourId().toString()));
        var activeTourToUpdate = result.get();
        try {
            var updatedActiveTour = activeTourRepository.save(activeTourToUpdate.updateInformation
                    (command.tourScheduleId(),
                            command.tourGuideId(),
                            command.currentLatitude(),
                            command.currentLongitude(),
                            command.startedAt(),
                            command.finishedAt(),
                            command.status()));
            return Result.success(updatedActiveTour);
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("update-active-tour", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(DeleteActiveTourCommand command) {
        if (!activeTourRepository.existsById(command.activeTourId())) {
            return Result.failure(ApplicationError.notFound("ActiveTour", command.activeTourId().toString()));
        }
        try {
            activeTourRepository.deleteById(command.activeTourId());
            return Result.success(command.activeTourId());
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("delete-activeTour", e.getMessage()));
        }
    }


}
