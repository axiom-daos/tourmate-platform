
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
 * Entidad JPA para almacenar comentarios en PostgreSQL.
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentPersistenceEntity
        extends AuditableAbstractPersistenceEntity {

    // Identificador de la reseña relacionada.
    @Column(
            name = "review_id",
            nullable = false,
            updatable = false
    )
    private Long reviewId;

    // Relación JPA de solo lectura con la reseña.
    @Getter(AccessLevel.NONE)
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

    // Texto escrito por el usuario.
    @Setter
    @Column(
            name = "content",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String content;

    /**
     * Constructor para registrar un nuevo comentario.
     */
    public CommentPersistenceEntity(
            Long reviewId,
            String content
    ) {
        this.reviewId = reviewId;
        this.content = content;
    }
}
