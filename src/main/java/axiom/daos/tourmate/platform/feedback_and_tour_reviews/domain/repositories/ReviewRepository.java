
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;

import java.util.List;
import java.util.Optional;

/**
 * Define las operaciones de persistencia de reseñas.
 */
public interface ReviewRepository {

    // Buscar una reseña por su identificador.
    Optional<Review> findById(Long id);

    // Obtener todas las reseñas.
    List<Review> findAll();

    // Obtener las reseñas de un tour.
    List<Review> findByTourId(Long tourId);

    // Verificar si existe una reseña.
    boolean existsById(Long id);

    // Verificar si un usuario ya calificó un tour.
    boolean existsByUserIdAndTourId(
            Long userId,
            Long tourId
    );

    // Verificar duplicados excluyendo una reseña.
    boolean existsByUserIdAndTourIdAndIdNot(
            Long userId,
            Long tourId,
            Long reviewId
    );

    // Crear o actualizar una reseña.
    Review save(Review review);

    // Eliminar una reseña por su identificador.
    void deleteById(Long id);
}
