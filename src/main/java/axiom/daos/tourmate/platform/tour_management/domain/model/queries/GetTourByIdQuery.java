package axiom.daos.tourmate.platform.tour_management.domain.model.queries;

public record GetTourByIdQuery(Long id) {

    public GetTourByIdQuery {
        if(id == null) {
            throw new IllegalArgumentException("Tour id is required");
        }

        if(id < 1) {
            throw new IllegalArgumentException("Tour id cannot be less than 1");
        }
    }
}
