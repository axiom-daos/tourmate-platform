package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.valueobjects;

/**
 * Value Object que representa la calificación
 * que un usuario asigna a un tour turístico.
 *
 * Una calificación válida debe ser un número
 * entero comprendido entre 1 y 5 estrellas.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * @param value Valor de la calificación.
 */
public record Rating(Integer value) {

    // Calificación mínima permitida.
    public static final int MIN_RATING = 1;

    // Calificación máxima permitida.
    public static final int MAX_RATING = 5;

    /**
     * Constructor compacto del Value Object.
     *
     * Comprueba que la calificación cumpla
     * las reglas del negocio.
     */
    public Rating {

        // Verificamos que el valor no sea nulo.
        if (value == null) {
            throw new IllegalArgumentException(
                    "La calificación no puede ser nula."
            );
        }

        // Comprobamos que esté entre 1 y 5.
        if (value < MIN_RATING || value > MAX_RATING) {
            throw new IllegalArgumentException(
                    "La calificación debe estar entre 1 y 5."
            );
        }
    }

    /**
     * Crea una instancia de Rating a partir
     * de un valor numérico.
     *
     * @param value Calificación recibida.
     * @return Un Rating válido.
     */
    public static Rating of(Integer value) {
        return new Rating(value);
    }

    /**
     * Devuelve la calificación como entero.
     *
     * Este método será útil al convertir
     * el Value Object en un dato de persistencia.
     *
     * @return Valor de la calificación.
     */
    public int asInt() {
        return value;
    }

    /**
     * Indica si la calificación es la máxima.
     *
     * @return true cuando equivale a 5 estrellas.
     */
    public boolean isMaximum() {
        return value == MAX_RATING;
    }

    /**
     * Indica si la calificación es la mínima.
     *
     * @return true cuando equivale a 1 estrella.
     */
    public boolean isMinimum() {
        return value == MIN_RATING;
    }
}
