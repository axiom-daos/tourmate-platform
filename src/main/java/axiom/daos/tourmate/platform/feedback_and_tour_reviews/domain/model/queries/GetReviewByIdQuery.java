
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries;

/**
 * Consulta para obtener una reseña específica
 * mediante su identificador único.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * @param reviewId Identificador de la reseña
 *                 que se desea consultar.
 */
public record GetReviewByIdQuery(
        Long reviewId
) {

    /**
     * Constructor compacto del Query.
     *
     * Comprueba que el identificador recibido
     * sea válido antes de realizar la consulta.
     *
     * @throws IllegalArgumentException
     * si el identificador es nulo o no es positivo.
     */
    public GetReviewByIdQuery {

        // Verificamos que el identificador no sea nulo
        // y que tenga un valor mayor que cero.
        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la reseña debe ser mayor que cero."
            );
        }
    }
}
