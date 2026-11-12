package axiom.daos.tourmate.platform.tour_management.domain.model.commands;

import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Coordinates;

public record AddCheckpointToTourCommand(
    Long tourId,
    String name,
    Coordinates location,
    int order,
    String description
) {
}
