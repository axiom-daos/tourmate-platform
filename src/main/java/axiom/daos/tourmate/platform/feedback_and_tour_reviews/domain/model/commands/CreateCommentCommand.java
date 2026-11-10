
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands;

/**
 * Comando para crear un comentario
 * asociado a una reseña turística.
 *
 * @param reviewId Identificador de la reseña.
 * @param content Contenido del comentario.
 */
public record CreateCommentCommand(
        Long reviewId,
        String content
) {

    /**
     * Constructor compacto que valida
     * los datos recibidos.
     */
    public CreateCommentCommand {

        // Verificamos que la reseña tenga un ID válido.
        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la reseña debe ser mayor que cero."
            );
        }

        // Verificamos que el comentario tenga
        // al menos cinco caracteres útiles.
        if (content == null || content.trim().length() < 5) {
            throw new IllegalArgumentException(
                    "El comentario debe tener al menos 5 caracteres."
            );
        }

        // Eliminamos espacios innecesarios
        // al inicio y al final del comentario.
        content = content.trim();
    }
}
