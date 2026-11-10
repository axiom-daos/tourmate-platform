
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.entities;

import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad de persistencia JPA para las reseñas.
 *
 * Representa la estructura de la tabla reviews
 * dentro de la base de datos PostgreSQL.
 *
 * Se mantiene separada del agregado Review
 * para respetar la arquitectura DDD.
 *
 * Bounded Context: Feedback and Tour Reviews.
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
@Setter
@NoArgsConstructor
public class ReviewPersistenceEntity
        extends AuditableAbstractPersistenceEntity {

    /**
     * Identificador del usuario que creó la reseña.
     *
     * Se almacena como Long para mantener
     * compatibilidad con el Bounded Context IAM.
     */
    @Column(
            name = "user_id",
            nullable = false
    )
    private Long userId;

    /**
     * Identificador del tour que se está calificando.
     *
     * Se almacena como Long para mantener
     * compatibilidad con Tour Management.
     */
    @Column(
            name = "tour_id",
            nullable = false
    )
    private Long tourId;

    /**
     * Calificación asignada al tour.
     *
     * El dominio valida que el valor
     * esté comprendido entre 1 y 5.
     */
    @Column(
            name = "rating",
            nullable = false
    )
    private Integer rating;

    /**
     * Constructor para crear una entidad
     * de persistencia a partir de sus datos.
     *
     * El identificador y las fechas de auditoría
     * son administrados por la clase base.
     *
     * @param userId Identificador del usuario.
     * @param tourId Identificador del tour.
     * @param rating Calificación de la reseña.
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
