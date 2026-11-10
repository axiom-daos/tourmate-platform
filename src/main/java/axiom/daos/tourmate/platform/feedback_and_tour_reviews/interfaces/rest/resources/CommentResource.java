package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources;

import java.time.Instant;

/**
 * Recurso REST que representa un comentario
 * asociado a una reseña turística de TourMate.
 *
 * Se utiliza para enviar la información
 * desde el backend hacia el frontend Angular.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * @param id Identificador único del comentario.
 * @param reviewId Identificador de la reseña.
 * @param content Contenido escrito por el usuario.
 * @param createdAt Fecha y hora de creación.
 */
public record CommentResource(
        Long id,
        Long reviewId,
        String content,
        Instant createdAt
) {

    /**
     * Este recurso representa los datos
     * que la API REST devuelve al frontend.
     *
     * No contiene lógica de negocio,
     * consultas SQL ni operaciones JPA.
     *
     * Los datos se obtendrán desde la entidad
     * de dominio Comment mediante la conversión
     * correspondiente en la capa REST.
     */
}
