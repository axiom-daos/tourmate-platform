package axiom.daos.tourmate.platform.tourmonitoring.domain.exceptions;

public class ActiveTourNotFoundException extends RuntimeException {
    public ActiveTourNotFoundException(Long activeTourId) {
        super(String.format("Active Tour with ID %s not found.", activeTourId));
    }
}
