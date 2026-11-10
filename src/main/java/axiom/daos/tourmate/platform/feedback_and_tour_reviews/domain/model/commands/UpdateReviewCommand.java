
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.valueobjects.Rating;

/**
 * Comando para actualizar la calificación de una reseña.
 */
public record UpdateReviewCommand(
        Long reviewId,
        Long userId,
        Long tourId,
        Integer rating
) {

    public UpdateReviewCommand {

        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la reseña debe ser mayor que cero."
            );
        }

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del usuario debe ser mayor que cero."
            );
        }

        if (tourId == null || tourId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del tour debe ser mayor que cero."
            );
        }

        // Permite calificaciones de 0 a 5.
        Rating.of(rating);
    }
}
