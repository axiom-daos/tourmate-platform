package axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources;

import java.time.LocalDateTime;

public record UpdateTourScheduleResource(
        LocalDateTime departureDateTime,
        int maxCapacity
) {
}
