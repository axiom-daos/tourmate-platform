
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources;

import java.time.Instant;

/**
 * Recurso REST que representa una reseña
 * turística registrada en TourMate.
 *
 * Se utiliza para devolver información
 * desde el backend hacia el frontend Angular.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * @param id Identificador único de la reseña.
 * @param userId Identificador del usuario.
 * @param tourId Identificador del tour.
 * @param rating Calificación de 1 a 5 estrellas.
 * @param createdAt Fecha y hora de creación.
 */
public record ReviewResource(
        Long id,
        Long userId,
        Long tourId,
        Integer rating,
        Instant createdAt
) {

    /**
     * Este recurso representa los datos
     * públicos de una reseña.
     *
     * No contiene lógica de negocio,
     * consultas JPA ni operaciones de persistencia.
     *
     * La conversión desde el agregado Review
     * será realizada por
     * ReviewResourceFromEntityAssembler.
     */
}
