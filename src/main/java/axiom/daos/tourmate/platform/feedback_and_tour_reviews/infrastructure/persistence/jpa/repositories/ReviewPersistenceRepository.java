
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.repositories;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.entities.ReviewPersistenceEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para las reseñas.
 *
 * Permite realizar operaciones de persistencia
 * sobre la tabla reviews de PostgreSQL.
 *
 * Trabaja con ReviewPersistenceEntity y
 * utiliza identificadores de tipo Long.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@Repository
public interface ReviewPersistenceRepository
        extends JpaRepository<ReviewPersistenceEntity, Long> {

    /**
     * Busca todas las reseñas asociadas
     * a un tour específico.
     *
     * Spring Data JPA genera automáticamente
     * la consulta utilizando el nombre del método.
     *
     * @param tourId Identificador del tour.
     * @return Lista de reseñas del tour.
     */
    List<ReviewPersistenceEntity> findByTourId(Long tourId);

    /**
     * Verifica si un usuario ya ha registrado
     * una reseña para un determinado tour.
     *
     * Esta consulta permite detectar
     * reseñas duplicadas antes de guardar.
     *
     * @param userId Identificador del usuario.
     * @param tourId Identificador del tour.
     * @return true si la reseña ya existe.
     */
    boolean existsByUserIdAndTourId(
            Long userId,
            Long tourId
    );

    /**
     * Comprueba si existe otra reseña del mismo
     * usuario y tour, excluyendo una reseña específica.
     *
     * Se utiliza al actualizar una reseña para
     * evitar duplicados.
     *
     * @param userId Identificador del usuario.
     * @param tourId Identificador del tour.
     * @param reviewId ID de la reseña que se excluye.
     * @return true si existe otra reseña.
     */
    boolean existsByUserIdAndTourIdAndIdNot(
            Long userId,
            Long tourId,
            Long reviewId
    );
}
