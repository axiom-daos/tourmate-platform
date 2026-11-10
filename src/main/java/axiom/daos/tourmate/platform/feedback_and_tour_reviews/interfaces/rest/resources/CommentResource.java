
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources;

import java.time.Instant;

/**
 * Representa un comentario enviado al frontend.
 */
public record CommentResource(
        Long id,
        Long reviewId,
        String content,
        Instant createdAt
) {
}
