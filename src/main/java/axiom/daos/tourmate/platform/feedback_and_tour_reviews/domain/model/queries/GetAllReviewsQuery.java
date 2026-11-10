package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries;

/**
 * Consulta para obtener todas las reseñas
 * registradas en la plataforma TourMate.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * Este Query no necesita parámetros porque
 * solicita la colección completa de reseñas.
 */
public record GetAllReviewsQuery() {

    /**
     * Constructor de la consulta.
     *
     * No requiere validaciones adicionales
     * porque no recibe ningún dato.
     */
}
