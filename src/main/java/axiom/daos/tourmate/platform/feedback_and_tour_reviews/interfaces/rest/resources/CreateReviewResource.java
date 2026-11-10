
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources;

/**
 * Datos recibidos para crear una reseña.
 */
public record CreateReviewResource(
        Long userId,
        Long tourId,
        Integer rating
) {
}
