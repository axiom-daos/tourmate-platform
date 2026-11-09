
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.queryservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetAllCommentsQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetCommentByIdQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetCommentsByReviewIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Contrato del servicio de consultas de comentarios.
 *
 * Define las operaciones de lectura disponibles
 * para los comentarios de reseñas turísticas.
 *
 * Siguiendo el patrón CQRS, este servicio
 * solamente consulta información y no modifica
 * los comentarios almacenados.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
public interface CommentQueryService {

    /**
     * Obtiene todos los comentarios registrados.
     *
     * @param query Consulta sin parámetros.
     * @return Lista de todos los comentarios.
     */
    List<Comment> handle(
            GetAllCommentsQuery query
    );

    /**
     * Busca un comentario mediante su identificador.
     *
     * @param query Contiene el ID del comentario.
     * @return Comentario encontrado o un Optional vacío.
     */
    Optional<Comment> handle(
            GetCommentByIdQuery query
    );

    /**
     * Obtiene todos los comentarios relacionados
     * con una reseña turística específica.
     *
     * @param query Contiene el ID de la reseña.
     * @return Lista de comentarios de esa reseña.
     */
    List<Comment> handle(
            GetCommentsByReviewIdQuery query
    );
}
