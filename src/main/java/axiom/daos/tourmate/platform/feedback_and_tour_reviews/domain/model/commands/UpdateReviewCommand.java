
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands;

/**
 * Comando para actualizar una reseña existente.
 *
 * Permite modificar la información de una reseña
 * respetando las reglas del negocio.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * @param reviewId Identificador de la reseña.
 * @param userId Identificador del usuario.
 * @param tourId Identificador del tour.
 * @param rating Nueva calificación de la reseña.
 */
public record UpdateReviewCommand(
        Long reviewId,
        Long userId,
        Long tourId,
        Integer rating
) {

    /**
     * Constructor compacto del comando.
     *
     * Comprueba que los identificadores sean
     * positivos y que la calificación sea válida.
     *
     * @throws IllegalArgumentException
     * cuando los datos no cumplen las reglas.
     */
    public UpdateReviewCommand {

        // La reseña debe existir y tener un ID válido.
        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la reseña debe ser mayor que cero."
            );
        }

        // Validamos el identificador del usuario.
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del usuario debe ser mayor que cero."
            );
        }

        // Validamos el identificador del tour.
        if (tourId == null || tourId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del tour debe ser mayor que cero."
            );
        }

        // Validamos la nueva calificación.
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException(
                    "La calificación debe estar entre 1 y 5."
            );
        }
    }
}
