
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.transform;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources.ReviewResource;

import java.util.Objects;

/**
 * Convierte una reseña del dominio en un recurso REST.
 */
public final class ReviewResourceFromEntityAssembler {

    private ReviewResourceFromEntityAssembler() {
    }

    public static ReviewResource toResourceFromEntity(
            Review review
    ) {

        Objects.requireNonNull(
                review,
                "La reseña no puede ser nula."
        );

        return new ReviewResource(
                review.getId(),
                review.getUserId(),
                review.getTourId(),
                review.getRating(),
                review.getCreatedAt()
        );
    }
}
