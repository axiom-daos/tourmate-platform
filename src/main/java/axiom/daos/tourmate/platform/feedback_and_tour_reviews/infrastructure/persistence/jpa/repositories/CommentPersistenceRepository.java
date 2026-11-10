
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.repositories;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.entities.CommentPersistenceEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de persistencia JPA para los comentarios.
 *
 * Permite realizar operaciones sobre la tabla
 * comments de PostgreSQL utilizando Spring Data JPA.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@Repository
public interface CommentPersistenceRepository
        extends JpaRepository<CommentPersistenceEntity, Long> {

    /**
     * Busca todos los comentarios asociados
     * a una reseña determinada.
     *
     * Spring Data JPA genera automáticamente
     * la consulta a partir del nombre del método.
     *
     * @param reviewId Identificador de la reseña.
     * @return Lista de comentarios relacionados.
     */
    List<CommentPersistenceEntity> findByReviewId(
            Long reviewId
    );

    /**
     * Elimina todos los comentarios asociados
     * a una reseña específica.
     *
     * Esta operación debe ejecutarse dentro
     * de una transacción.
     *
     * @param reviewId Identificador de la reseña.
     */
    void deleteByReviewId(
            Long reviewId
    );
}
