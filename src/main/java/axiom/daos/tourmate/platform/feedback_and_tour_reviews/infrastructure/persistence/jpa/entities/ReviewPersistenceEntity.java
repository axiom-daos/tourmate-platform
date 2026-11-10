
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.entities;

import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA para almacenar reseñas en PostgreSQL.
 */
@Entity
@Table(
        name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reviews_user_tour",
                        columnNames = {"user_id", "tour_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewPersistenceEntity
        extends AuditableAbstractPersistenceEntity {

    // Usuario que registró la reseña.
    @Column(
            name = "user_id",
            nullable = false,
            updatable = false
    )
    private Long userId;

    // Tour que recibió la calificación.
    @Column(
            name = "tour_id",
            nullable = false,
            updatable = false
    )
    private Long tourId;

    // Calificación de 0 a 5 estrellas.
    @Setter
    @Column(
            name = "rating",
            nullable = false
    )
    private Integer rating;

    /**
     * Constructor para registrar una nueva reseña.
     */
    public ReviewPersistenceEntity(
            Long userId,
            Long tourId,
            Integer rating
    ) {
        this.userId = userId;
        this.tourId = tourId;
        this.rating = rating;
    }
}
