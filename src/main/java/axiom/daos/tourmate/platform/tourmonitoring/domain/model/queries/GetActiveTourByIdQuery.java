package axiom.daos.tourmate.platform.tourmonitoring.domain.model.queries;

public record GetActiveTourByIdQuery(Long activeTourId) {
    public GetActiveTourByIdQuery{
        if (activeTourId == null || activeTourId <= 0) {
            throw new IllegalArgumentException("activeTourId cannot be null or less than 1");
        }
    }
}
