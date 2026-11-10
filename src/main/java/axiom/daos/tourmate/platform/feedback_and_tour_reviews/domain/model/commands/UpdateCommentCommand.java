
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands;

/**
 * Comando para actualizar un comentario
 * existente dentro de TourMate.
 *
 * @param commentId Identificador del comentario.
 * @param reviewId Identificador de la reseña relacionada.
 * @param content Nuevo contenido del comentario.
 */
public record UpdateCommentCommand(
        Long commentId,
        Long reviewId,
        String content
) {

    /**
     * Constructor compacto encargado de
     * comprobar las reglas de validación.
     */
    public UpdateCommentCommand {

        // Validamos el identificador del comentario.
        if (commentId == null || commentId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del comentario debe ser mayor que cero."
            );
        }

        // Validamos el identificador de la reseña.
        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la reseña debe ser mayor que cero."
            );
        }

        // Validamos el contenido actualizado.
        if (content == null || content.trim().length() < 5) {
            throw new IllegalArgumentException(
                    "El comentario debe tener al menos 5 caracteres."
            );
        }

        // Guardamos el contenido sin espacios externos.
        content = content.trim();
    }
}
