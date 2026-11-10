
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries;

/**
 * Consulta para buscar un comentario por su ID.
 */
public record GetCommentByIdQuery(
        Long commentId
) {

    public GetCommentByIdQuery {

        if (commentId == null || commentId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del comentario debe ser mayor que cero."
            );
        }
    }
}
