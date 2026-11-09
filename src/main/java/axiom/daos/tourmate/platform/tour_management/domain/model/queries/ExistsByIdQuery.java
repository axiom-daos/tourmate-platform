package axiom.daos.tourmate.platform.tour_management.domain.model.queries;

public record ExistsByIdQuery(Long id) {

    public ExistsByIdQuery {
        if(id == null) {
            throw new IllegalArgumentException("Id is required");
        }

        if(id < 1) {
            throw new IllegalArgumentException("Id cannot be less than 1");
        }
    }
}
