
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Datos recibidos para crear una reseña.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateReviewResource(
        Long userId,
        Long tourId,
        Integer rating
) {
}
