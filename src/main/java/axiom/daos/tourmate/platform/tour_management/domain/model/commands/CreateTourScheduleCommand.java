package axiom.daos.tourmate.platform.tour_management.domain.model.commands;

import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.DepartureDateTime;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourId;

public record CreateTourScheduleCommand(TourId tourId, DepartureDateTime departureDateTime, int maxCapacity) {
}
