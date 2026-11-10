
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands;

/**
 * Comando para eliminar un comentario existente.
 *
 * Representa la solicitud de eliminación
 * de un comentario asociado a una reseña.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * @param commentId Identificador del comentario
 *                  que se desea eliminar.
 */
public record DeleteCommentCommand(
        Long commentId
) {

    /**
     * Constructor compacto del comando.
     *
     * Valida que el identificador del comentario
     * sea válido antes de procesar la eliminación.
     */
    public DeleteCommentCommand {

        // Verificamos que el identificador no sea nulo.
        if (commentId == null) {
            throw new IllegalArgumentException(
                    "El identificador del comentario no puede ser nulo."
            );
        }

        // Verificamos que el identificador sea positivo.
        if (commentId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del comentario debe ser mayor que cero."
            );
        }
    }
}
