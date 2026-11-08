package axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects;

public record CheckpointId(Long value) {

    public CheckpointId {

        if(value == null || value < 1) {
            throw new IllegalArgumentException("Value cannot be null or less than 1");
        }
    }
}
