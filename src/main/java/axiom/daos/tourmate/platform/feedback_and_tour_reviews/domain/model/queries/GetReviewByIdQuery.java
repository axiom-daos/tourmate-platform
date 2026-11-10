
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries;

/**
 * Consulta para buscar una reseña por su identificador.
 */
public record GetReviewByIdQuery(
        Long reviewId
) {

    public GetReviewByIdQuery {

        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la reseña debe ser mayor que cero."
            );
        }
    }
}
