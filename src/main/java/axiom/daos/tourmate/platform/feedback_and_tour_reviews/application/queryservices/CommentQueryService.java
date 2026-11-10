
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.queryservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetAllCommentsQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetCommentByIdQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetCommentsByReviewIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Define las consultas disponibles para los comentarios.
 */
public interface CommentQueryService {

    // Obtener todos los comentarios.
    List<Comment> handle(
            GetAllCommentsQuery query
    );

    // Buscar un comentario por su ID.
    Optional<Comment> handle(
            GetCommentByIdQuery query
    );

    // Obtener los comentarios de una reseña.
    List<Comment> handle(
            GetCommentsByReviewIdQuery query
    );
}
