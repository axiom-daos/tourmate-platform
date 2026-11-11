package axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources;


import axiom.daos.tourmate.platform.shared.interfaces.rest.resources.MoneyResource;

public record TourDetailsResource(
        String title,
        String description,
        String duration,
        String difficultyLevel,
        MoneyResource price
) {
}
