package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates;

import axiom.daos.tourmate.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Agregado principal de las reseñas turísticas.
 *
 * Representa la calificación que un usuario
 * realiza sobre un tour de TourMate.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@Getter
public class Review extends AbstractDomainAggregateRoot<Review> {

    // Identificador único de la reseña.
    private Long id;

    // Identificador del usuario que realiza la reseña.
    private Long userId;

    // Identificador del tour que se está calificando.
    private Long tourId;

    // Calificación del tour: de 1 a 5 estrellas.
    private Integer rating;

    // Fecha y hora de creación de la reseña.
    private Instant createdAt;

    /**
     * Constructor para registrar una nueva reseña.
     *
     * El identificador será asignado posteriormente
     * por la base de datos.
     *
     * @param userId Identificador del usuario.
     * @param tourId Identificador del tour.
     * @param rating Calificación entre 1 y 5.
     */
    public Review(Long userId, Long tourId, Integer rating) {

        // Comprobamos las reglas del negocio.
        validateUserId(userId);
        validateTourId(tourId);
        validateRating(rating);

        // Inicializamos los datos de la reseña.
        this.id = null;
        this.userId = userId;
        this.tourId = tourId;
        this.rating = rating;
        this.createdAt = Instant.now();
    }

    /**
     * Reconstruye una reseña existente desde
     * los datos almacenados en PostgreSQL.
     *
     * Esta operación será utilizada por el
     * adaptador de persistencia.
     */
    public static Review reconstitute(
            Long id,
            Long userId,
            Long tourId,
            Integer rating,
            Instant createdAt
    ) {

        // Validamos el identificador almacenado.
        validateId(id);

        // La fecha almacenada no debe ser nula.
        Objects.requireNonNull(
                createdAt,
                "La fecha de creación es obligatoria."
        );

        // Construimos el agregado con sus reglas.
        Review review = new Review(userId, tourId, rating);

        // Recuperamos los valores originales.
        review.id = id;
        review.createdAt = createdAt;

        return review;
    }

    /**
     * Valida el identificador de una reseña existente.
     */
    private static void validateId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de la reseña es inválido."
            );
        }
    }

    /**
     * Comprueba que el usuario tenga un ID válido.
     */
    private static void validateUserId(Long userId) {

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del usuario es inválido."
            );
        }
    }

    /**
     * Comprueba que el tour tenga un ID válido.
     */
    private static void validateTourId(Long tourId) {

        if (tourId == null || tourId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del tour es inválido."
            );
        }
    }

    /**
     * Valida que la calificación sea de 1 a 5.
     */
    private static void validateRating(Integer rating) {

        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException(
                    "La calificación debe estar entre 1 y 5."
            );
        }
    }

    /**
     * Actualiza la información de una reseña.
     *
     * Permite modificar el usuario, el tour
     * y la calificación, respetando las reglas.
     *
     * Las autorizaciones y los duplicados serán
     * comprobados en los servicios correspondientes.
     */
    public Review updateInformation(
            Long userId,
            Long tourId,
            Integer rating
    ) {

        // Validamos antes de modificar los datos.
        validateUserId(userId);
        validateTourId(tourId);
        validateRating(rating);

        // Actualizamos las propiedades.
        this.userId = userId;
        this.tourId = tourId;
        this.rating = rating;

        return this;
    }

    /**
     * Modifica únicamente la calificación.
     */
    public Review updateRating(Integer rating) {

        validateRating(rating);

        this.rating = rating;

        return this;
    }
}
