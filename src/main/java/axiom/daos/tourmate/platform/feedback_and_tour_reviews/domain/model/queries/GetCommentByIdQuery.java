
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries;

/**
 * Consulta para obtener un comentario específico
 * mediante su identificador único.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * @param commentId Identificador del comentario
 *                  que se desea consultar.
 */
public record GetCommentByIdQuery(
        Long commentId
) {

    /**
     * Constructor compacto del Query.
     *
     * Comprueba que el identificador recibido
     * sea válido antes de realizar la consulta.
     */
    public GetCommentByIdQuery {

        // Validamos que el identificador no sea nulo.
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
