
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries;

/**
 * Consulta para obtener todos los comentarios
 * registrados en la plataforma TourMate.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * Este Query no recibe parámetros porque
 * solicita la colección completa de comentarios.
 */
public record GetAllCommentsQuery() {

    /**
     * No necesita validaciones adicionales
     * porque no recibe ningún argumento.
     */
}
