
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources;

/**
 * Recurso REST utilizado para solicitar
 * la creación de una nueva reseña turística.
 *
 * Representa los datos recibidos desde
 * el frontend Angular mediante una petición HTTP.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * @param userId Identificador del usuario.
 * @param tourId Identificador del tour.
 * @param rating Calificación de 1 a 5 estrellas.
 */
public record CreateReviewResource(
        Long userId,
        Long tourId,
        Integer rating
) {
    /**
     * La validación de los identificadores
     * y de la calificación será realizada
     * por CreateReviewCommand.
     *
     * Este recurso solamente transporta
     * los datos de entrada de la API REST.
     */
}
