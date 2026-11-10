
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands;

/**
 * Comando para crear un comentario asociado a una reseña.
 */
public record CreateCommentCommand(
        Long reviewId,
        String content
) {

    public CreateCommentCommand {

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

        // Eliminamos los espacios de los extremos.
        content = content.trim();
    }
}
