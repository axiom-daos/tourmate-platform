package axiom.daos.tourmate.platform.tour_management.application.internal.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.tour_management.application.commandservices.TourCommandService;
import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.Tour;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.AddCheckpointToTourCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.CreateTourCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.DeleteTourCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.UpdateTourCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.entities.Checkpoint;
import axiom.daos.tourmate.platform.tour_management.domain.repositories.TourRepository;
import org.springframework.stereotype.Service;

@Service
public class TourCommandServiceImpl implements TourCommandService {

    private final TourRepository tourRepository;

    public TourCommandServiceImpl(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    @Override
    public Result<Tour, ApplicationError> handle(CreateTourCommand command) {

        if(this.tourRepository.existsByTitle(command.tourDetails().title())) {
            return Result.failure(ApplicationError.conflict("Tour", "Title %s already exists".formatted(command.tourDetails().title())));
        }

        var tour = new Tour(command.agencyId(), command.tourDetails());

        try {
            tour = this.tourRepository.save(tour);
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-tour", e.getMessage()));
        }

        return Result.success(tour);
    }

    @Override
    public Result<Tour, ApplicationError> handle(UpdateTourCommand command) {

        var result = this.tourRepository.findById(command.tourId());

        if(result.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Tour", command.tourId().toString()));
        }

        var tourToUpdate = result.get();

        try {
            var tourUpdated = this.tourRepository.save(tourToUpdate.changeDetails(command.tourDetails()));

            return Result.success(tourToUpdate);
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("update-tour", e.getMessage()));
        }
    }


    @Override
    public Result<Long, ApplicationError> handle(DeleteTourCommand command) {
        if(!this.tourRepository.existsById(command.id())) {
            return Result.failure(ApplicationError.notFound("Tour", command.id().toString()));
        }

        try {
            this.tourRepository.deleteById(command.id());
            return Result.success(command.id());
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("delete-tour", e.getMessage()));
        }
    }

    @Override
    public Result<Tour, ApplicationError> handle(AddCheckpointToTourCommand command) {

        var result = this.tourRepository.findById(command.tourId());

        if(result.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Tour", command.tourId().toString()));
        }

        var tour = result.get();

        var newCheckpoint = new Checkpoint(
                command.name(),
                command.location(),
                command.order(),
                command.description());

        tour.addCheckpoint(newCheckpoint);

        try {
            var tourSaved = this.tourRepository.save(tour);
            return Result.success(tourSaved);

        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("add-checkpoint", e.getMessage()));
        }

    }
}
