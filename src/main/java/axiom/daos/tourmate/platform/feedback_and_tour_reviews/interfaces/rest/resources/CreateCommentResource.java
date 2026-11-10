
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources;

/**
 * Datos recibidos para crear un comentario.
 */
public record CreateCommentResource(
        Long reviewId,
        String content
) {
}
