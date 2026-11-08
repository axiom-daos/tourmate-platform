package axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects;

import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Money;

public record TourDetails(String title, String description, String duration, DifficultyLevel difficultyLevel, Money price) {

    public TourDetails {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
        if (duration == null || duration.isBlank()) {
            throw new IllegalArgumentException("Duration cannot be null or blank");
        }
        if (difficultyLevel == null) {
            throw new IllegalArgumentException("Difficulty level required");
        }
        if (price == null) {
            throw new IllegalArgumentException("Price required");
        }

        title = title.trim();
        description = description.trim();
        duration = duration.trim();
    }
}
