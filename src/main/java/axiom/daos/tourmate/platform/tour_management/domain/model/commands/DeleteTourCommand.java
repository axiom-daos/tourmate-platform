package axiom.daos.tourmate.platform.tour_management.domain.model.commands;

public record DeleteTourCommand(Long id) {

    public DeleteTourCommand {
        if(id == null) {
            throw new IllegalArgumentException("Tour id is required");
        }

        if(id < 1) {
            throw new IllegalArgumentException("Tour id cannot be less than 1");
        }
    }
}
