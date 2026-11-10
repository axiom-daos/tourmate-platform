
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.repositories;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.entities.ReviewPersistenceEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la tabla reviews.
 */
@Repository
public interface ReviewPersistenceRepository
        extends JpaRepository<ReviewPersistenceEntity, Long> {

    // Obtiene las reseñas de un tour.
    List<ReviewPersistenceEntity> findByTourId(Long tourId);

    // Comprueba si el usuario ya calificó el tour.
    boolean existsByUserIdAndTourId(
            Long userId,
            Long tourId
    );

    // Busca duplicados excluyendo una reseña específica.
    boolean existsByUserIdAndTourIdAndIdNot(
            Long userId,
            Long tourId,
            Long reviewId
    );
}
