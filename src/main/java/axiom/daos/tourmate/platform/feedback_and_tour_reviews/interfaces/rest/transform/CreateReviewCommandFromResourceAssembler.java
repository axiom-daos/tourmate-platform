
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.transform;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources.CreateReviewResource;

/**
 * Convierte los datos REST en un comando de creación.
 */
public final class CreateReviewCommandFromResourceAssembler {

    private CreateReviewCommandFromResourceAssembler() {
    }

    /**
     * Construye el comando con los datos recibidos.
     */
    public static CreateReviewCommand toCommandFromResource(
            CreateReviewResource resource
    ) {

        if (resource == null) {
            throw new IllegalArgumentException(
                    "Los datos de la reseña son obligatorios."
            );
        }

        return new CreateReviewCommand(
                resource.userId(),
                resource.tourId(),
                resource.rating()
        );
    }
}
