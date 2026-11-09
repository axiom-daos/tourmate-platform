package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries;

/**
 * Consulta para obtener todas las reseñas
 * asociadas a un tour específico de TourMate.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * @param tourId Identificador del tour
 *               cuyas reseñas se desean consultar.
 */
public record GetReviewsByTourIdQuery(
        Long tourId
) {

    /**
     * Constructor compacto del Query.
     *
     * Valida que el identificador del tour
     * sea un número positivo.
     *
     * @throws IllegalArgumentException
     * si el identificador es nulo o no es positivo.
     */
    public GetReviewsByTourIdQuery {

        // Validamos que el tour tenga un identificador válido.
        if (tourId == null || tourId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del tour debe ser mayor que cero."
            );
        }
    }
}
