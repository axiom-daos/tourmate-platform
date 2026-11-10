
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.queryservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetAllReviewsQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetReviewByIdQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetReviewsByTourIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Contrato del servicio de consultas de reseñas.
 *
 * Define las operaciones de lectura disponibles
 * para el Bounded Context Feedback and Tour Reviews.
 *
 * Siguiendo CQRS, este servicio únicamente
 * consulta información y no modifica reseñas.
 */
public interface ReviewQueryService {

    /**
     * Obtiene todas las reseñas registradas.
     *
     * @param query Consulta sin parámetros.
     * @return Lista de todas las reseñas.
     */
    List<Review> handle(GetAllReviewsQuery query);

    /**
     * Busca una reseña mediante su identificador.
     *
     * @param query Contiene el ID de la reseña.
     * @return La reseña encontrada o un Optional vacío.
     */
    Optional<Review> handle(GetReviewByIdQuery query);

    /**
     * Obtiene las reseñas asociadas a un tour.
     *
     * @param query Contiene el ID del tour.
     * @return Lista de reseñas del tour solicitado.
     */
    List<Review> handle(GetReviewsByTourIdQuery query);
}
