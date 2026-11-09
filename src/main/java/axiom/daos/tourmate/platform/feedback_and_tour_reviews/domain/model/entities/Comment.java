
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities;

import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Entidad de dominio que representa
 * un comentario asociado a una reseña turística.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@Getter
public class Comment {

    // Identificador único del comentario.
    private Long id;

    // Identificador de la reseña relacionada.
    private Long reviewId;

    // Contenido escrito por el usuario.
    private String content;

    // Fecha y hora de creación del comentario.
    private Instant createdAt;

    /**
     * Constructor para crear un comentario nuevo.
     *
     * El ID será generado por la base de datos
     * cuando se guarde el comentario.
     *
     * @param reviewId Identificador de la reseña.
     * @param content Contenido del comentario.
     */
    public Comment(Long reviewId, String content) {

        // Validamos los datos recibidos.
        validateReviewId(reviewId);
        validateContent(content);

        // Inicializamos las propiedades.
        this.id = null;
        this.reviewId = reviewId;
        this.content = content.trim();
        this.createdAt = Instant.now();
    }

    /**
     * Reconstruye un comentario existente
     * utilizando los datos almacenados.
     *
     * Será utilizado por los adaptadores
     * de la capa infrastructure.
     */
    public static Comment reconstitute(
            Long id,
            Long reviewId,
            String content,
            Instant createdAt
    ) {

        // Comprobamos el identificador del comentario.
        validateId(id);

        // La fecha no debe ser nula.
        Objects.requireNonNull(
                createdAt,
                "La fecha de creación es obligatoria."
        );

        // Creamos el comentario validando sus datos.
        Comment comment = new Comment(reviewId, content);

        // Recuperamos los valores de persistencia.
        comment.id = id;
        comment.createdAt = createdAt;

        return comment;
    }

    /**
     * Valida el identificador de un comentario.
     */
    private static void validateId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del comentario es inválido."
            );
        }
    }

    /**
     * Comprueba que el comentario pertenezca
     * a una reseña con identificador válido.
     */
    private static void validateReviewId(Long reviewId) {

        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la reseña es inválido."
            );
        }
    }

    /**
     * Valida el contenido del comentario.
     *
     * Debe contener al menos cinco caracteres
     * sin contar los espacios de los extremos.
     */
    private static void validateContent(String content) {

        if (content == null || content.trim().length() < 5) {
            throw new IllegalArgumentException(
                    "El comentario debe tener al menos 5 caracteres."
            );
        }
    }

    /**
     * Actualiza el contenido de un comentario.
     *
     * Conserva el ID, la reseña relacionada
     * y la fecha original de creación.
     */
    public Comment updateContent(String content) {

        // Comprobamos las reglas antes de actualizar.
        validateContent(content);

        // Guardamos el contenido sin espacios innecesarios.
        this.content = content.trim();

        return this;
    }
}
