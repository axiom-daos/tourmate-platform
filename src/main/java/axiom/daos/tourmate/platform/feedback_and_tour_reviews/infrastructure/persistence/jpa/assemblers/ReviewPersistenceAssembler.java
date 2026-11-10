
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.entities.ReviewPersistenceEntity;

import java.util.Objects;

/**
 * Convierte reseñas entre el dominio y la persistencia JPA.
 */
public final class ReviewPersistenceAssembler {

    private ReviewPersistenceAssembler() {
    }

    /**
     * Reconstruye una reseña almacenada en PostgreSQL.
     */
    public static Review toDomainFromPersistence(
            ReviewPersistenceEntity entity
    ) {

        if (entity == null) {
            return null;
        }

        Objects.requireNonNull(
                entity.getCreatedAt(),
                "La reseña almacenada no tiene fecha de creación."
        );

        return Review.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getTourId(),
                entity.getRating(),
                entity.getCreatedAt().toInstant()
        );
    }

    /**
     * Prepara una reseña nueva para guardarla.
     */
    public static ReviewPersistenceEntity toPersistenceFromDomain(
            Review review
    ) {

        Objects.requireNonNull(
                review,
                "La reseña no puede ser nula."
        );

        if (review.getId() != null) {
            throw new IllegalArgumentException(
                    "La reseña ya tiene un identificador."
            );
        }

        return new ReviewPersistenceEntity(
                review.getUserId(),
                review.getTourId(),
                review.getRating()
        );
    }

    /**
     * Actualiza únicamente la calificación.
     */
    public static ReviewPersistenceEntity updatePersistenceFromDomain(
            Review review,
            ReviewPersistenceEntity entity
    ) {

        Objects.requireNonNull(
                review,
                "La reseña no puede ser nula."
        );

        Objects.requireNonNull(
                entity,
                "La entidad JPA no puede ser nula."
        );

        if (review.getId() == null ||
                !review.getId().equals(entity.getId())) {

            throw new IllegalArgumentException(
                    "Los identificadores de la reseña no coinciden."
            );
        }

        // El usuario y el tour deben conservarse.
        if (!Objects.equals(review.getUserId(), entity.getUserId()) ||
                !Objects.equals(review.getTourId(), entity.getTourId())) {

            throw new IllegalArgumentException(
                    "No se puede cambiar el usuario ni el tour."
            );
        }

        // Solo modificamos las estrellas.
        entity.setRating(review.getRating());

        return entity;
    }
}
