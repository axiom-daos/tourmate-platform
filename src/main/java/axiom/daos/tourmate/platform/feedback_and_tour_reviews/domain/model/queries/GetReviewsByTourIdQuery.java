
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries;

/**
 * Consulta para obtener las reseñas de un tour.
 */
public record GetReviewsByTourIdQuery(
        Long tourId
) {

    public GetReviewsByTourIdQuery {

        if (tourId == null || tourId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del tour debe ser mayor que cero."
            );
        }
    }
}
