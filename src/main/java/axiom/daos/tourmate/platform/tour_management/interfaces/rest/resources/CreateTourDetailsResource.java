package axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources;

import axiom.daos.tourmate.platform.shared.interfaces.rest.resources.CreateMoneyResource;
import jakarta.validation.constraints.NotNull;


public record CreateTourDetailsResource(

        @NotNull(message = "Title is required")
        String title,

        @NotNull(message = "Description is required")
        String description,

        @NotNull(message = "Duration is required")
        String duration,

        @NotNull(message = "Difficulty level is required")
        String difficultyLevel,

        @NotNull(message = "Price is required")
        CreateMoneyResource price


) {
}
