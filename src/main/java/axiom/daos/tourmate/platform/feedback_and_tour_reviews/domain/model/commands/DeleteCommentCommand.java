
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands;

/**
 * Comando para eliminar un comentario.
 */
public record DeleteCommentCommand(
        Long commentId
) {

    public DeleteCommentCommand {

        if (commentId == null || commentId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del comentario debe ser mayor que cero."
            );
        }
    }
}
