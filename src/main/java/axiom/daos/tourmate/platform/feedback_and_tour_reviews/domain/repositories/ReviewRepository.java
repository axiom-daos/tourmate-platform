package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de dominio para las reseñas.
 *
 * Define las operaciones necesarias para
 * almacenar, consultar y eliminar reseñas.
 *
 * No contiene código SQL ni anotaciones JPA.
 * Su implementación estará en infrastructure.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
public interface ReviewRepository {

    /**
     * Busca una reseña mediante su identificador.
     *
     * @param id Identificador de la reseña.
     * @return La reseña encontrada o un Optional vacío.
     */
    Optional<Review> findById(Long id);

    /**
     * Obtiene todas las reseñas registradas.
     *
     * @return Lista de reseñas.
     */
    List<Review> findAll();

    /**
     * Obtiene las reseñas asociadas
     * a un determinado tour turístico.
     *
     * @param tourId Identificador del tour.
     * @return Lista de reseñas del tour.
     */
    List<Review> findByTourId(Long tourId);

    /**
     * Comprueba si una reseña existe
     * utilizando su identificador.
     *
     * @param id Identificador de la reseña.
     * @return true si existe.
     */
    boolean existsById(Long id);

    /**
     * Comprueba si un usuario ya registró
     * una reseña para un tour específico.
     *
     * Permite prevenir reseñas duplicadas.
     *
     * @param userId Identificador del usuario.
     * @param tourId Identificador del tour.
     * @return true si ya existe una reseña.
     */
    boolean existsByUserIdAndTourId(
            Long userId,
            Long tourId
    );

    /**
     * Comprueba si existe otra reseña del mismo
     * usuario y tour, excluyendo la reseña actual.
     *
     * Es útil durante las actualizaciones.
     *
     * @param userId Identificador del usuario.
     * @param tourId Identificador del tour.
     * @param reviewId Identificador que se excluye.
     * @return true si existe otra reseña.
     */
    boolean existsByUserIdAndTourIdAndIdNot(
            Long userId,
            Long tourId,
            Long reviewId
    );

    /**
     * Guarda una reseña nueva o actualiza
     * una reseña existente.
     *
     * @param review Agregado de dominio.
     * @return Reseña almacenada.
     */
    Review save(Review review);

    /**
     * Elimina una reseña mediante su ID.
     *
     * @param id Identificador de la reseña.
     */
    void deleteById(Long id);
}
