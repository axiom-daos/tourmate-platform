
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries;

/**
 * Consulta para obtener los comentarios de una reseña.
 */
public record GetCommentsByReviewIdQuery(
        Long reviewId
) {

    public GetCommentsByReviewIdQuery {

        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la reseña debe ser mayor que cero."
            );
        }
    }
}
