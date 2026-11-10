
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.valueobjects;

/**
 * Representa una calificación de 0 a 5 estrellas.
 */
public record Rating(Integer value) {

    public static final int MIN_RATING = 0;
    public static final int MAX_RATING = 5;

    public Rating {
        if (value == null ||
                value < MIN_RATING ||
                value > MAX_RATING) {

            throw new IllegalArgumentException(
                    "La calificación debe estar entre 0 y 5."
            );
        }
    }

    public static Rating of(Integer value) {
        return new Rating(value);
    }

    public int asInt() {
        return value;
    }

    public boolean isMaximum() {
        return value == MAX_RATING;
    }

    public boolean isMinimum() {
        return value == MIN_RATING;
    }
}
