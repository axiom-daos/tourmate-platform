
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries;

/**
 * Consulta para obtener todos los comentarios
 * asociados a una reseña turística específica.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * @param reviewId Identificador de la reseña
 *                 cuyos comentarios se consultarán.
 */
public record GetCommentsByReviewIdQuery(
        Long reviewId
) {

    /**
     * Constructor compacto del Query.
     *
     * Comprueba que el identificador de la reseña
     * sea válido antes de procesar la consulta.
     */
    public GetCommentsByReviewIdQuery {

        // Validamos que el ID de la reseña no sea nulo.
        if (reviewId == null) {
            throw new IllegalArgumentException(
                    "El identificador de la reseña no puede ser nulo."
            );
        }

        // Comprobamos que el ID sea mayor que cero.
        if (reviewId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la reseña debe ser mayor que cero."
            );
        }
    }
}
