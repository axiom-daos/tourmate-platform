
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.adapters;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.ReviewRepository;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.assemblers.ReviewPersistenceAssembler;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.entities.ReviewPersistenceEntity;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.repositories.ReviewPersistenceRepository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Adaptador de persistencia para el agregado Review.
 *
 * Implementa el contrato ReviewRepository del dominio
 * utilizando el repositorio de Spring Data JPA.
 *
 * Se encarga de convertir los datos entre
 * el dominio y la infraestructura.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@Repository
@Transactional(readOnly = true)
public class ReviewRepositoryImpl implements ReviewRepository {

    // Repositorio JPA que realiza las operaciones
    // de persistencia sobre la tabla reviews.
    private final ReviewPersistenceRepository persistenceRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * Spring proporciona automáticamente
     * el repositorio JPA correspondiente.
     *
     * @param persistenceRepository Repositorio JPA.
     */
    public ReviewRepositoryImpl(
            ReviewPersistenceRepository persistenceRepository
    ) {
        this.persistenceRepository = persistenceRepository;
    }

    /**
     * Busca una reseña mediante su identificador.
     *
     * Convierte la entidad JPA recuperada
     * en un agregado del dominio.
     *
     * @param id Identificador de la reseña.
     * @return Reseña encontrada o un Optional vacío.
     */
    @Override
    public Optional<Review> findById(Long id) {

        // Consultamos el registro en PostgreSQL
        // y lo convertimos en un agregado Review.
        return persistenceRepository.findById(id)
                .map(
                        ReviewPersistenceAssembler
                                ::toDomainFromPersistence
                );
    }

    /**
     * Obtiene todas las reseñas registradas.
     *
     * @return Lista de agregados Review.
     */
    @Override
    public List<Review> findAll() {

        // Recuperamos todas las entidades JPA.
        return persistenceRepository.findAll()
                .stream()

                // Convertimos cada entidad JPA
                // en un agregado del dominio.
                .map(
                        ReviewPersistenceAssembler
                                ::toDomainFromPersistence
                )
                .toList();
    }

    /**
     * Obtiene todas las reseñas asociadas
     * a un tour determinado.
     *
     * @param tourId Identificador del tour.
     * @return Lista de reseñas del tour.
     */
    @Override
    public List<Review> findByTourId(Long tourId) {

        // Buscamos las reseñas del tour
        // y convertimos los resultados al dominio.
        return persistenceRepository.findByTourId(tourId)
                .stream()
                .map(
                        ReviewPersistenceAssembler
                                ::toDomainFromPersistence
                )
                .toList();
    }

    /**
     * Comprueba si una reseña existe.
     *
     * @param id Identificador de la reseña.
     * @return true cuando existe el registro.
     */
    @Override
    public boolean existsById(Long id) {

        // Delegamos la búsqueda al repositorio JPA.
        return persistenceRepository.existsById(id);
    }

    /**
     * Comprueba si un usuario ya registró
     * una reseña para un tour específico.
     *
     * @param userId Identificador del usuario.
     * @param tourId Identificador del tour.
     * @return true cuando existe una reseña.
     */
    @Override
    public boolean existsByUserIdAndTourId(
            Long userId,
            Long tourId
    ) {

        // Verificamos si existe una reseña
        // con esta combinación de usuario y tour.
        return persistenceRepository.existsByUserIdAndTourId(
                userId,
                tourId
        );
    }

    /**
     * Comprueba si existe otra reseña del mismo
     * usuario y tour, excluyendo la reseña actual.
     *
     * @param userId Identificador del usuario.
     * @param tourId Identificador del tour.
     * @param reviewId ID que se debe excluir.
     * @return true cuando existe otra reseña.
     */
    @Override
    public boolean existsByUserIdAndTourIdAndIdNot(
            Long userId,
            Long tourId,
            Long reviewId
    ) {

        // Delegamos la comprobación de duplicados
        // al repositorio de persistencia.
        return persistenceRepository
                .existsByUserIdAndTourIdAndIdNot(
                        userId,
                        tourId,
                        reviewId
                );
    }

    /**
     * Guarda una nueva reseña o actualiza
     * una reseña existente.
     *
     * @param review Agregado de dominio.
     * @return Reseña almacenada con sus datos.
     */
    @Override
    @Transactional
    public Review save(Review review) {

        // Comprobamos que la reseña exista.
        Objects.requireNonNull(
                review,
                "La reseña no puede ser nula."
        );

        ReviewPersistenceEntity entity;

        // Si no tiene ID, estamos creando una reseña nueva.
        if (review.getId() == null) {

            // Convertimos el agregado nuevo
            // en una entidad de persistencia.
            entity = ReviewPersistenceAssembler
                    .toPersistenceFromDomain(review);

        } else {

            // Si tiene ID, buscamos el registro existente.
            ReviewPersistenceEntity existingEntity =
                    persistenceRepository.findById(review.getId())
                            .orElseThrow(
                                    () -> new IllegalArgumentException(
                                            "No existe la reseña con ID: "
                                                    + review.getId()
                                    )
                            );

            // Aplicamos los cambios del dominio
            // sobre la entidad JPA existente.
            entity = ReviewPersistenceAssembler
                    .updatePersistenceFromDomain(
                            review,
                            existingEntity
                    );
        }

        // Guardamos y sincronizamos los cambios
        // con la base de datos.
        ReviewPersistenceEntity savedEntity =
                persistenceRepository.saveAndFlush(entity);

        // Convertimos el registro guardado
        // nuevamente en un agregado del dominio.
        return ReviewPersistenceAssembler
                .toDomainFromPersistence(savedEntity);
    }

    /**
     * Elimina una reseña mediante su identificador.
     *
     * @param id Identificador de la reseña.
     */
    @Override
    @Transactional
    public void deleteById(Long id) {

        // Solicitamos la eliminación del registro.
        persistenceRepository.deleteById(id);
    }
}
