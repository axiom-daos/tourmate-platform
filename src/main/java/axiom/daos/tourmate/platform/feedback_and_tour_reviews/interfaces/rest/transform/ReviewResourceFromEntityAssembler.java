
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.transform;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources.ReviewResource;

import java.util.Objects;

/**
 * Ensamblador que convierte un agregado Review
 * en un recurso de respuesta REST.
 *
 * Permite que el backend devuelva los datos
 * de una reseña al frontend Angular sin
 * exponer directamente el agregado del dominio.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
public final class ReviewResourceFromEntityAssembler {

    /**
     * Constructor privado.
     *
     * Impide crear instancias innecesarias,
     * porque el ensamblador utiliza métodos estáticos.
     */
    private ReviewResourceFromEntityAssembler() {
    }

    /**
     * Convierte un agregado del dominio
     * en un recurso REST de reseña.
     *
     * @param review Agregado Review del dominio.
     * @return Recurso preparado para la respuesta HTTP.
     */
    public static ReviewResource toResourceFromEntity(
            Review review
    ) {

        // Comprobamos que la reseña no sea nula.
        Objects.requireNonNull(
                review,
                "La reseña no puede ser nula."
        );

        // Construimos el recurso con los datos
        // que necesita el frontend Angular.
        return new ReviewResource(
                review.getId(),
                review.getUserId(),
                review.getTourId(),
                review.getRating(),
                review.getCreatedAt()
        );
    }
}
