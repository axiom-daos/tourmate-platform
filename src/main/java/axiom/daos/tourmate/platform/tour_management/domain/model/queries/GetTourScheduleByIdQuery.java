package axiom.daos.tourmate.platform.tour_management.domain.model.queries;

public record GetTourScheduleByIdQuery(Long id) {

    public GetTourScheduleByIdQuery {
        if(id == null) {
            throw new IllegalArgumentException("Tour schedule id is required");
        }

        if(id < 1) {
            throw new IllegalArgumentException("Tour schedule id cannot be less than 1");
        }
    }
}
