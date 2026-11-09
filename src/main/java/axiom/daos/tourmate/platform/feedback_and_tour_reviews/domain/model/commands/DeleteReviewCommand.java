package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands;

/**
 * Comando para eliminar una reseña existente.
 *
 * Representa la solicitud de eliminación de
 * una reseña dentro de TourMate.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * @param reviewId Identificador de la reseña
 *                 que se desea eliminar.
 */
public record DeleteReviewCommand(
        Long reviewId
) {

    /**
     * Constructor compacto del comando.
     *
     * Comprueba que el identificador recibido
     * sea válido antes de procesar la eliminación.
     *
     * @throws IllegalArgumentException
     * si el identificador es nulo o no es positivo.
     */
    public DeleteReviewCommand {

        // Verificamos que la reseña tenga un ID válido.
        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la reseña debe ser mayor que cero."
            );
        }
    }
}
