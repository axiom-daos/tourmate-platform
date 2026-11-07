package axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands;

public record DeleteActiveTourCommand(Long activeTourId) {
    public DeleteActiveTourCommand{
        if (activeTourId == null || activeTourId <= 0) {
            throw new IllegalArgumentException("activeTourId cannot be null or less than 1");
        }
    }
}
