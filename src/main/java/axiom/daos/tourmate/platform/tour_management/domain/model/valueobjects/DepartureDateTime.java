package axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects;

import java.time.LocalDateTime;

public record DepartureDateTime(LocalDateTime value) {

    public DepartureDateTime {
        if(value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
    }
}
