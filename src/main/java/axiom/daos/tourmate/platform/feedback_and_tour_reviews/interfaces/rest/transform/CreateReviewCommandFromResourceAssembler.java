
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.transform;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources.CreateReviewResource;

import java.util.Objects;

/**
 * Ensamblador que convierte un recurso REST
 * en un comando de creación de reseñas.
 *
 * Permite separar los datos recibidos desde
 * Angular de los objetos del dominio.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
public final class CreateReviewCommandFromResourceAssembler {

    /**
     * Constructor privado.
     *
     * Evita crear instancias de esta clase,
     * porque utilizaremos métodos estáticos.
     */
    private CreateReviewCommandFromResourceAssembler() {
    }

    /**
     * Convierte un recurso REST en un comando
     * para registrar una nueva reseña.
     *
     * @param resource Datos enviados desde Angular.
     * @return Comando de creación validado.
     */
    public static CreateReviewCommand toCommandFromResource(
            CreateReviewResource resource
    ) {

        // Verificamos que el recurso recibido exista.
        Objects.requireNonNull(
                resource,
                "El recurso de creación no puede ser nulo."
        );

        // Construimos el comando con los datos recibidos.
        // El constructor de CreateReviewCommand
        // validará los IDs y la calificación.
        return new CreateReviewCommand(
                resource.userId(),
                resource.tourId(),
                resource.rating()
        );
    }
}
