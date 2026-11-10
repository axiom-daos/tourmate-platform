
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands;

/**
 * Comando para actualizar un comentario existente.
 */
public record UpdateCommentCommand(
        Long commentId,
        Long reviewId,
        String content
) {

    public UpdateCommentCommand {

        if (commentId == null || commentId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del comentario debe ser mayor que cero."
            );
        }

        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la reseña debe ser mayor que cero."
            );
        }

        if (content == null || content.trim().length() < 5) {
            throw new IllegalArgumentException(
                    "El comentario debe tener al menos 5 caracteres."
            );
        }

        // Conservamos el contenido sin espacios externos.
        content = content.trim();
    }
}
