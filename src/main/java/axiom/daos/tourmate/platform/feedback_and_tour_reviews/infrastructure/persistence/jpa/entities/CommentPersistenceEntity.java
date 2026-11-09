package axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.entities;

import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad de persistencia JPA para los comentarios.
 *
 * Representa la estructura de la tabla comments
 * dentro de la base de datos PostgreSQL.
 *
 * Se mantiene separada de la entidad de dominio
 * Comment para respetar la arquitectura DDD.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class CommentPersistenceEntity
        extends AuditableAbstractPersistenceEntity {

    /**
     * Identificador de la reseña a la que
     * pertenece este comentario.
     *
     * Utilizamos Long para mantener la misma
     * estructura de identificadores del backend.
     */
    @Column(
            name = "review_id",
            nullable = false
    )
    private Long reviewId;

    /**
     * Contenido escrito por el usuario.
     *
     * Utilizamos TEXT de PostgreSQL para
     * almacenar comentarios de longitud variable.
     */
    @Column(
            name = "content",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String content;

    /**
     * Constructor para crear un nuevo
     * registro de comentario.
     *
     * El ID y las fechas de auditoría
     * son gestionados por la clase base.
     *
     * @param reviewId Identificador de la reseña.
     * @param content Contenido del comentario.
     */
    public CommentPersistenceEntity(
            Long reviewId,
            String content
    ) {
        this.reviewId = reviewId;
        this.content = content;
    }
}
