
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities;

import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Representa un comentario asociado a una reseña.
 */
@Getter
public class Comment {

    private Long id;

    // La reseña relacionada no puede modificarse.
    private final Long reviewId;

    private String content;
    private Instant createdAt;

    /**
     * Crea un comentario nuevo.
     */
    public Comment(
            Long reviewId,
            String content
    ) {

        validateReviewId(reviewId);
        validateContent(content);

        this.id = null;
        this.reviewId = reviewId;
        this.content = content.trim();
        this.createdAt = Instant.now();
    }

    /**
     * Recupera un comentario almacenado en PostgreSQL.
     */
    public static Comment reconstitute(
            Long id,
            Long reviewId,
            String content,
            Instant createdAt
    ) {

        validateId(id);

        Objects.requireNonNull(
                createdAt,
                "La fecha de creación es obligatoria."
        );

        Comment comment = new Comment(
                reviewId,
                content
        );

        comment.id = id;
        comment.createdAt = createdAt;

        return comment;
    }

    /**
     * Actualiza únicamente el texto del comentario.
     */
    public Comment updateContent(String content) {

        validateContent(content);

        this.content = content.trim();

        return this;
    }

    private static void validateId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del comentario es inválido."
            );
        }
    }

    private static void validateReviewId(Long reviewId) {

        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la reseña es inválido."
            );
        }
    }

    private static void validateContent(String content) {

        if (content == null || content.trim().length() < 5) {
            throw new IllegalArgumentException(
                    "El comentario debe tener al menos 5 caracteres."
            );
        }
    }
}
