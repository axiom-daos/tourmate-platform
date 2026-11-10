
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.entities;

import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad de persistencia JPA para los comentarios.
 *
 * Representa la tabla comments de PostgreSQL.
 *
 * Reglas:
 * - Cada comentario pertenece a una reseña existente.
 * - Cada reseña puede tener como máximo un comentario.
 * - La relación se protege mediante una clave foránea.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@Entity
@Table(
        name = "comments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_comments_review_id",
                        columnNames = {"review_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class CommentPersistenceEntity
        extends AuditableAbstractPersistenceEntity {

    /**
     * Identificador de la reseña relacionada.
     *
     * Conservamos este campo de tipo Long
     * para mantener la compatibilidad con
     * nuestros repositorios y assemblers.
     *
     * UNIQUE impide múltiples comentarios
     * para una misma reseña.
     */
    @Column(
            name = "review_id",
            nullable = false
    )
    private Long reviewId;

    /**
     * Relación JPA con la reseña existente.
     *
     * Se utiliza para declarar la clave foránea
     * entre comments.review_id y reviews.id.
     *
     * La relación es de solo lectura porque
     * reviewId administra la columna.
     *
     * No utilizamos cascadas: la eliminación
     * de comentarios ya se gestiona desde
     * ReviewCommandServiceImpl.
     *
     * La restricción UNIQUE garantiza que
     * una reseña tenga como máximo un comentario.
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "review_id",
            referencedColumnName = "id",
            nullable = false,
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(
                    name = "fk_comments_review"
            )
    )
    private ReviewPersistenceEntity review;

    /**
     * Contenido escrito por el usuario.
     *
     * Utilizamos TEXT para almacenar
     * comentarios de longitud variable.
     */
    @Column(
            name = "content",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String content;

    /**
     * Constructor para registrar un comentario.
     *
     * La base de datos generará el identificador
     * y las fechas de auditoría.
     *
     * @param reviewId Identificador de la reseña.
     * @param content Texto del comentario.
     */
    public CommentPersistenceEntity(
            Long reviewId,
            String content
    ) {
        this.reviewId = reviewId;
        this.content = content;
    }
}
