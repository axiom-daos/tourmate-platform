
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.queryservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetAllReviewsQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetReviewByIdQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetReviewsByTourIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Define las consultas disponibles para las reseñas.
 */
public interface ReviewQueryService {

    // Obtener todas las reseñas.
    List<Review> handle(
            GetAllReviewsQuery query
    );

    // Buscar una reseña por su ID.
    Optional<Review> handle(
            GetReviewByIdQuery query
    );

    // Obtener las reseñas de un tour.
    List<Review> handle(
            GetReviewsByTourIdQuery query
    );
}
