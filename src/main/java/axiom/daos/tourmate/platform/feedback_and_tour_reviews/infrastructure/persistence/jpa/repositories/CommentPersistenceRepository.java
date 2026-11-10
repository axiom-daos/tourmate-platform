
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.repositories;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.entities.CommentPersistenceEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la tabla comments.
 */
@Repository
public interface CommentPersistenceRepository
        extends JpaRepository<CommentPersistenceEntity, Long> {

    // Obtiene los comentarios de una reseña.
    List<CommentPersistenceEntity> findByReviewId(
            Long reviewId
    );

    // Elimina los comentarios asociados a una reseña.
    void deleteByReviewId(Long reviewId);
}
