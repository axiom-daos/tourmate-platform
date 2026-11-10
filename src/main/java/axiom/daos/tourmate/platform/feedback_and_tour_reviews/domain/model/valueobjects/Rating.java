
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.valueobjects;

/**
 * Value Object que representa la calificación
 * de una reseña turística.
 *
 * Una calificación válida debe ser un número
 * entero comprendido entre 1 y 5 estrellas.
 *
 * Este objeto es inmutable y centraliza
 * las reglas de validación de las calificaciones.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * @param value Valor numérico de la calificación.
 */
public record Rating(Integer value) {

    // Calificación mínima permitida.
    public static final int MIN_RATING = 1;

    // Calificación máxima permitida.
    public static final int MAX_RATING = 5;

    /**
     * Constructor compacto del Value Object.
     *
     * Comprueba que la calificación sea válida
     * antes de crear una instancia.
     */
    public Rating {

        // La calificación es obligatoria.
        if (value == null) {
            throw new IllegalArgumentException(
                    "La calificación no puede ser nula."
            );
        }

        // Solo permitimos valores entre 1 y 5.
        if (value < MIN_RATING || value > MAX_RATING) {
            throw new IllegalArgumentException(
                    "La calificación debe estar entre 1 y 5."
            );
        }
    }

    /**
     * Crea una calificación a partir
     * de un valor numérico.
     *
     * @param value Calificación recibida.
     * @return Value Object validado.
     */
    public static Rating of(Integer value) {
        return new Rating(value);
    }

    /**
     * Obtiene la calificación como un entero.
     *
     * Se utiliza para convertir el Value Object
     * en datos que pueda manejar JPA o la API REST.
     *
     * @return Calificación numérica.
     */
    public int asInt() {
        return value;
    }

    /**
     * Indica si la calificación es la máxima.
     *
     * @return true cuando tiene 5 estrellas.
     */
    public boolean isMaximum() {
        return value == MAX_RATING;
    }

    /**
     * Indica si la calificación es la mínima.
     *
     * @return true cuando tiene 1 estrella.
     */
    public boolean isMinimum() {
        return value == MIN_RATING;
    }
}
