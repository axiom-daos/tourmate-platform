
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.valueobjects.Rating;
import axiom.daos.tourmate.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import lombok.AccessLevel;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Agregado principal de las reseñas turísticas.
 */
@Getter
public class Review extends AbstractDomainAggregateRoot<Review> {

    private Long id;

    private final Long userId;
    private final Long tourId;

    @Getter(AccessLevel.NONE)
    private Rating rating;

    private Instant createdAt;

    /**
     * Crea una nueva reseña.
     */
    public Review(
            Long userId,
            Long tourId,
            Integer rating
    ) {

        validateUserId(userId);
        validateTourId(tourId);

        this.id = null;
        this.userId = userId;
        this.tourId = tourId;
        this.rating = Rating.of(rating);
        this.createdAt = Instant.now();
    }

    /**
     * Reconstruye una reseña almacenada en PostgreSQL.
     */
    public static Review reconstitute(
            Long id,
            Long userId,
            Long tourId,
            Integer rating,
            Instant createdAt
    ) {

        validateId(id);

        Objects.requireNonNull(
                createdAt,
                "La fecha de creación es obligatoria."
        );

        Review review = new Review(
                userId,
                tourId,
                rating
        );

        review.id = id;
        review.createdAt = createdAt;

        return review;
    }

    /**
     * Devuelve la calificación como Integer.
     */
    public Integer getRating() {
        return rating.value();
    }

    /**
     * Actualiza únicamente las estrellas.
     */
    public Review updateRating(Integer rating) {

        this.rating = Rating.of(rating);

        return this;
    }

    private static void validateId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la reseña es inválido."
            );
        }
    }

    private static void validateUserId(Long userId) {

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del usuario es inválido."
            );
        }
    }

    private static void validateTourId(Long tourId) {

        if (tourId == null || tourId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del tour es inválido."
            );
        }
    }
}
