
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Datos recibidos para crear un comentario.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateCommentResource(
        Long reviewId,
        String content
) {
}
