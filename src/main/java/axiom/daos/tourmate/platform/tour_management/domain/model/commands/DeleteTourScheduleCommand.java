package axiom.daos.tourmate.platform.tour_management.domain.model.commands;

public record DeleteTourScheduleCommand(Long id) {
    public DeleteTourScheduleCommand {
        if(id == null) {
            throw new IllegalArgumentException("Tour schedule id is required");
        }

        if(id < 1) {
            throw new IllegalArgumentException("Tour schedule id cannot be less than 1");
        }
    }
}
