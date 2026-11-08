package axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects;

public record TourId(Long value) {

    public TourId {

        if(value == null || value < 1) {
            throw new IllegalArgumentException("Value cannot be null or less than 1");
        }
    }
}
