
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources;

/**
 * Recurso REST utilizado para actualizar
 * una reseña turística existente.
 *
 * Representa los datos recibidos desde
 * el frontend Angular mediante una petición PUT.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * @param userId Identificador del usuario.
 * @param tourId Identificador del tour.
 * @param rating Nueva calificación de 1 a 5 estrellas.
 */
public record UpdateReviewResource(
        Long userId,
        Long tourId,
        Integer rating
) {

    /**
     * Este recurso únicamente transporta
     * los datos de la petición HTTP.
     *
     * Las reglas de validación se encuentran
     * en UpdateReviewCommand y en el dominio.
     *
     * El identificador de la reseña se recibirá
     * desde la URL del endpoint REST.
     */
}
