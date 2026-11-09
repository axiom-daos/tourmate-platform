package axiom.daos.tourmate.platform.tour_management.domain.model.commands;

import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.AgencyId;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourDetails;

public record CreateTourCommand(AgencyId agencyId, TourDetails tourDetails) {
}
