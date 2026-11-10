
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
 * Adaptador de persistencia para las reseñas.
 */
@Repository
@Transactional(readOnly = true)
public class ReviewRepositoryImpl implements ReviewRepository {

    private final ReviewPersistenceRepository persistenceRepository;

    public ReviewRepositoryImpl(
            ReviewPersistenceRepository persistenceRepository
    ) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public Optional<Review> findById(Long id) {
        return persistenceRepository.findById(id)
                .map(ReviewPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Review> findAll() {
        return persistenceRepository.findAll()
                .stream()
                .map(ReviewPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Review> findByTourId(Long tourId) {
        return persistenceRepository.findByTourId(tourId)
                .stream()
                .map(ReviewPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return persistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByUserIdAndTourId(
            Long userId,
            Long tourId
    ) {
        return persistenceRepository.existsByUserIdAndTourId(
                userId,
                tourId
        );
    }

    @Override
    public boolean existsByUserIdAndTourIdAndIdNot(
            Long userId,
            Long tourId,
            Long reviewId
    ) {
        return persistenceRepository.existsByUserIdAndTourIdAndIdNot(
                userId,
                tourId,
                reviewId
        );
    }

    /**
     * Guarda una reseña nueva o actualiza su calificación.
     */
    @Override
    @Transactional
    public Review save(Review review) {

        Objects.requireNonNull(
                review,
                "La reseña no puede ser nula."
        );

        ReviewPersistenceEntity entity;

        if (review.getId() == null) {

            // Nueva reseña.
            entity = ReviewPersistenceAssembler
                    .toPersistenceFromDomain(review);

        } else {

            // Reseña existente.
            ReviewPersistenceEntity existingEntity =
                    persistenceRepository.findById(review.getId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "No existe la reseña con ID: "
                                                    + review.getId()
                                    )
                            );

            entity = ReviewPersistenceAssembler
                    .updatePersistenceFromDomain(
                            review,
                            existingEntity
                    );
        }

        ReviewPersistenceEntity savedEntity =
                persistenceRepository.saveAndFlush(entity);

        return ReviewPersistenceAssembler
                .toDomainFromPersistence(savedEntity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        persistenceRepository.deleteById(id);
    }
}
