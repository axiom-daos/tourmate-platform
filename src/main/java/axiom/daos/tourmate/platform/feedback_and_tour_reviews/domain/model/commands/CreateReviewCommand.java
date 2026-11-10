
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.valueobjects.Rating;

/**
 * Comando para registrar una nueva reseña.
 */
public record CreateReviewCommand(
        Long userId,
        Long tourId,
        Integer rating
) {

    public CreateReviewCommand {

        // Validar el usuario.
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del usuario debe ser mayor que cero."
            );
        }

        // Validar el tour.
        if (tourId == null || tourId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del tour debe ser mayor que cero."
            );
        }

        // Validar la calificación.
        Rating.of(rating);
    }
}
