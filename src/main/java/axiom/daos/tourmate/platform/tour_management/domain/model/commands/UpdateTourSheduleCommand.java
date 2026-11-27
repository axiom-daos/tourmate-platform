package axiom.daos.tourmate.platform.tour_management.domain.model.commands;

import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourScheduleStatus;

import java.time.LocalDateTime;

public record UpdateTourSheduleCommand(
        Long id,
        LocalDateTime departureDateTime,
        int maxCapacity) {
}
