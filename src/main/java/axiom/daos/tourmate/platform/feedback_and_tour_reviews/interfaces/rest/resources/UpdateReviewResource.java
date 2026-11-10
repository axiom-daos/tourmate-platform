
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources;

/**
 * Datos recibidos para actualizar una reseña.
 */
public record UpdateReviewResource(
        Long userId,
        Long tourId,
        Integer rating
) {
}
