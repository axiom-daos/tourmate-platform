
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RatingTest {

    @Test
    void shouldAcceptAllRatingsFromZeroToFive() {
        for (int value = 0; value <= 5; value++) {
            Rating rating = Rating.of(value);

            assertEquals(Integer.valueOf(value), rating.value());
            assertEquals(value, rating.asInt());
        }
    }

    @Test
    void shouldRejectNegativeRating() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Rating.of(-1)
        );
    }

    @Test
    void shouldRejectRatingGreaterThanFive() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Rating.of(6)
        );
    }

    @Test
    void shouldRejectNullRating() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Rating.of(null)
        );
    }

    @Test
    void shouldRecognizeMinimumAndMaximum() {
        Rating minimum = Rating.of(0);
        Rating maximum = Rating.of(5);
        Rating middle = Rating.of(3);

        assertTrue(minimum.isMinimum());
        assertFalse(minimum.isMaximum());

        assertTrue(maximum.isMaximum());
        assertFalse(maximum.isMinimum());

        assertFalse(middle.isMinimum());
        assertFalse(middle.isMaximum());
    }

    @Test
    void shouldCompareRatingsByValue() {
        Rating first = Rating.of(4);
        Rating second = Rating.of(4);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(Integer.valueOf(4), first.value());
    }
}
