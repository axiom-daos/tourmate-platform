
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.valueobjects.Rating;

import axiom.daos.tourmate.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import lombok.AccessLevel;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Agregado principal de las reseñas turísticas.
 *
 * Representa la calificación que un usuario
 * realiza sobre un tour de TourMate.
 *
 * Utiliza el Value Object Rating para garantizar
 * que las calificaciones cumplan las reglas
 * establecidas por el negocio.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@Getter
public class Review extends AbstractDomainAggregateRoot<Review> {

    // Identificador único de la reseña.
    private Long id;

    // Identificador del usuario propietario.
    // No puede modificarse después de la creación.
    private final Long userId;

    // Identificador del tour calificado.
    // No puede modificarse después de la creación.
    private final Long tourId;

    // Value Object que contiene la calificación.
    // Evitamos generar un getter automático porque
    // debemos conservar el contrato Integer.
    @Getter(AccessLevel.NONE)
    private Rating rating;

    // Fecha original de creación de la reseña.
    private Instant createdAt;

    /**
     * Constructor para registrar una nueva reseña.
     *
     * El identificador se asignará posteriormente
     * cuando la reseña se guarde en PostgreSQL.
     *
     * @param userId Identificador del usuario.
     * @param tourId Identificador del tour.
     * @param rating Calificación entre 1 y 5.
     */
    public Review(
            Long userId,
            Long tourId,
            Integer rating
    ) {

        // Comprobamos los identificadores.
        validateUserId(userId);
        validateTourId(tourId);

        // Inicializamos los datos de la reseña.
        this.id = null;
        this.userId = userId;
        this.tourId = tourId;

        // Creamos el Value Object.
        // Rating valida automáticamente el valor.
        this.rating = Rating.of(rating);

        // Registramos la fecha de creación.
        this.createdAt = Instant.now();
    }

    /**
     * Reconstruye una reseña existente utilizando
     * los datos recuperados desde PostgreSQL.
     *
     * Conserva el ID y la fecha de creación original.
     *
     * @param id Identificador de la reseña.
     * @param userId Usuario propietario.
     * @param tourId Tour relacionado.
     * @param rating Calificación almacenada.
     * @param createdAt Fecha original de creación.
     * @return Agregado reconstruido.
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

        // Comprobamos la fecha de creación.
        Objects.requireNonNull(
                createdAt,
                "La fecha de creación es obligatoria."
        );

        // Creamos el agregado aplicando las
        // mismas validaciones del dominio.
        Review review = new Review(
                userId,
                tourId,
                rating
        );

        // Recuperamos los valores originales.
        review.id = id;
        review.createdAt = createdAt;

        return review;
    }

    /**
     * Valida el identificador de una reseña.
     *
     * @param id Identificador recibido.
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
     *
     * @param userId Identificador del usuario.
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
     *
     * @param tourId Identificador del tour.
     */
    private static void validateTourId(Long tourId) {

        if (tourId == null || tourId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del tour es inválido."
            );
        }
    }

    /**
     * Obtiene la calificación como Integer.
     *
     * Conservamos este método para mantener
     * la compatibilidad con los assemblers,
     * los repositorios y los recursos REST.
     *
     * @return Calificación entre 1 y 5.
     */
    public Integer getRating() {

        // Convertimos el Value Object en un entero.
        return rating.value();
    }

    /**
     * Actualiza únicamente la calificación.
     *
     * El usuario, el tour y la fecha de creación
     * permanecen sin modificaciones.
     *
     * @param rating Nueva calificación.
     * @return Agregado actualizado.
     */
    public Review updateRating(Integer rating) {

        // Creamos un nuevo Value Object validado.
        // Si el valor es inválido, se lanza
        // una excepción antes de modificar el estado.
        this.rating = Rating.of(rating);

        return this;
    }
}
