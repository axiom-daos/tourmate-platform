
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.entities.CommentPersistenceEntity;

import java.util.Objects;

/**
 * Convierte comentarios entre el dominio y la persistencia JPA.
 */
public final class CommentPersistenceAssembler {

    private CommentPersistenceAssembler() {
    }

    /**
     * Reconstruye un comentario almacenado en PostgreSQL.
     */
    public static Comment toDomainFromPersistence(
            CommentPersistenceEntity entity
    ) {

        if (entity == null) {
            return null;
        }

        Objects.requireNonNull(
                entity.getCreatedAt(),
                "El comentario almacenado no tiene fecha de creación."
        );

        return Comment.reconstitute(
                entity.getId(),
                entity.getReviewId(),
                entity.getContent(),
                entity.getCreatedAt().toInstant()
        );
    }

    /**
     * Prepara un comentario nuevo para guardarlo.
     */
    public static CommentPersistenceEntity toPersistenceFromDomain(
            Comment comment
    ) {

        Objects.requireNonNull(
                comment,
                "El comentario no puede ser nulo."
        );

        if (comment.getId() != null) {
            throw new IllegalArgumentException(
                    "El comentario ya tiene un identificador."
            );
        }

        return new CommentPersistenceEntity(
                comment.getReviewId(),
                comment.getContent()
        );
    }

    /**
     * Actualiza únicamente el contenido del comentario.
     */
    public static CommentPersistenceEntity updatePersistenceFromDomain(
            Comment comment,
            CommentPersistenceEntity entity
    ) {

        Objects.requireNonNull(
                comment,
                "El comentario no puede ser nulo."
        );

        Objects.requireNonNull(
                entity,
                "La entidad JPA no puede ser nula."
        );

        if (comment.getId() == null ||
                !comment.getId().equals(entity.getId())) {

            throw new IllegalArgumentException(
                    "Los identificadores del comentario no coinciden."
            );
        }

        // El comentario debe mantener su reseña original.
        if (!Objects.equals(
                comment.getReviewId(),
                entity.getReviewId()
        )) {

            throw new IllegalArgumentException(
                    "No se puede cambiar la reseña del comentario."
            );
        }

        // Solo modificamos el texto.
        entity.setContent(comment.getContent());

        return entity;
    }
}
