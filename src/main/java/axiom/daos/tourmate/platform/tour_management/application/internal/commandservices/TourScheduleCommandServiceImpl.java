package axiom.daos.tourmate.platform.tour_management.application.internal.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.tour_management.application.commandservices.TourScheduleCommandService;
import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.TourSchedule;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.CloseTourScheduleCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.CreateTourScheduleCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.DeleteTourScheduleCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.OpenTourScheduleCommand;
import axiom.daos.tourmate.platform.tour_management.domain.repositories.TourScheduleRepository;
import org.springframework.stereotype.Service;

import javax.naming.spi.DirStateFactory;

@Service
public class TourScheduleCommandServiceImpl implements TourScheduleCommandService {

    private final TourScheduleRepository tourScheduleRepository;

    public TourScheduleCommandServiceImpl(TourScheduleRepository tourScheduleRepository) {
        this.tourScheduleRepository = tourScheduleRepository;
    }

    @Override
    public Result<TourSchedule, ApplicationError> handle(CreateTourScheduleCommand command) {

        if(this.tourScheduleRepository.existsByTourIdAndDepartureDateTime(command.tourId(), command.departureDateTime())) {
            return Result.failure(ApplicationError.conflict(
                    "Tour-schedule",
                    "Tour Schedule with %s ID and departure datetime at %s already exists".formatted(command.tourId().value().toString(), command.departureDateTime().value().toString())));
        }

        var tourSchedule = new TourSchedule(command.tourId(), command.departureDateTime(), command.maxCapacity());

        try {
            tourSchedule = this.tourScheduleRepository.save(tourSchedule);
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-tour-schedule", e.getMessage()));
        }

        return Result.success(tourSchedule);
    }

    @Override
    public Result<Long, ApplicationError> handle(DeleteTourScheduleCommand command) {

        if(!this.tourScheduleRepository.existsById(command.id())) {
            return Result.failure(ApplicationError.notFound("Tour-schedule", command.id().toString()));
        }

        try {
            this.tourScheduleRepository.deleteById(command.id());
            return Result.success(command.id());
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("delete-tour-schedule", e.getMessage()));
        }
    }

    @Override
    public Result<TourSchedule, ApplicationError> handle(OpenTourScheduleCommand command) {

        var result = this.tourScheduleRepository.findById(command.id());

        if(result.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Tour-Schedule", command.id().toString()));
        }

        var tourSchedule = result.get();

        try {
            tourSchedule.open();
            var tourScheduleSaved = this.tourScheduleRepository.save(tourSchedule);
            return Result.success(tourScheduleSaved);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.conflict("open-tour", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("open-tour", e.getMessage()));
        }


    }

    @Override
    public Result<TourSchedule, ApplicationError> handle(CloseTourScheduleCommand command) {
        var result = this.tourScheduleRepository.findById(command.id());

        if(result.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Tour-Schedule", command.id().toString()));
        }

        var tourSchedule = result.get();

        try {
            tourSchedule.close();
            var tourScheduleSaved = this.tourScheduleRepository.save(tourSchedule);
            return Result.success(tourScheduleSaved);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.conflict("close-tour", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("close-tour", e.getMessage()));
        }
    }
}
