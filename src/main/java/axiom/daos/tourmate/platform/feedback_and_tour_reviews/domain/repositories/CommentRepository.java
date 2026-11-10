
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;

import java.util.List;
import java.util.Optional;

/**
 * Define las operaciones de persistencia de comentarios.
 */
public interface CommentRepository {

    // Buscar un comentario por su identificador.
    Optional<Comment> findById(Long id);

    // Obtener todos los comentarios.
    List<Comment> findAll();

    // Obtener los comentarios de una reseña.
    List<Comment> findByReviewId(Long reviewId);

    // Verificar si existe un comentario.
    boolean existsById(Long id);

    // Crear o actualizar un comentario.
    Comment save(Comment comment);

    // Eliminar un comentario por su identificador.
    void deleteById(Long id);

    // Eliminar los comentarios asociados a una reseña.
    void deleteByReviewId(Long reviewId);
}
