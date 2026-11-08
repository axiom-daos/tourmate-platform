package axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects;

public record UserId(Long value) {

    public UserId {
        if(value == null || value < 1) {
            throw new IllegalArgumentException("Value cannot be null or less than 1");
        }
    }
}
