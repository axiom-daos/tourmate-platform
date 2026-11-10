package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources;
import java.time.Instant;
/**
 * Representa una reseña enviada al frontend.
 */
public record ReviewResource(
        Long id,
        Long userId,
        Long tourId,
        Integer rating,
        Instant createdAt
) {
}
