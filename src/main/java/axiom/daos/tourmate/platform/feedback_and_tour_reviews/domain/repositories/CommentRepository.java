
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de dominio para los comentarios.
 *
 * Define las operaciones necesarias para consultar,
 * guardar y eliminar comentarios de las reseñas.
 *
 * Esta interfaz no contiene consultas SQL ni
 * anotaciones de persistencia JPA.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
public interface CommentRepository {

    /**
     * Busca un comentario por su identificador.
     *
     * @param id Identificador del comentario.
     * @return Comentario encontrado o un Optional vacío.
     */
    Optional<Comment> findById(Long id);

    /**
     * Obtiene todos los comentarios registrados.
     *
     * @return Lista de comentarios.
     */
    List<Comment> findAll();

    /**
     * Busca todos los comentarios asociados
     * a una reseña específica.
     *
     * @param reviewId Identificador de la reseña.
     * @return Lista de comentarios relacionados.
     */
    List<Comment> findByReviewId(Long reviewId);

    /**
     * Comprueba si existe un comentario.
     *
     * @param id Identificador del comentario.
     * @return true si existe; false en caso contrario.
     */
    boolean existsById(Long id);

    /**
     * Guarda un comentario nuevo o actualiza
     * uno que ya existe en la base de datos.
     *
     * @param comment Entidad de dominio.
     * @return Comentario almacenado.
     */
    Comment save(Comment comment);

    /**
     * Elimina un comentario mediante su ID.
     *
     * @param id Identificador del comentario.
     */
    void deleteById(Long id);

    /**
     * Elimina todos los comentarios relacionados
     * con una reseña específica.
     *
     * Esta operación será útil cuando
     * se elimine una reseña.
     *
     * @param reviewId Identificador de la reseña.
     */
    void deleteByReviewId(Long reviewId);
}
