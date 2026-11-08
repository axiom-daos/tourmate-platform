package axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects;

public record AgencyId(Long value) {

    public AgencyId {

        if(value == null || value < 1) {
            throw new IllegalArgumentException("Value cannot be null or less than 1");
        }
    }
}
