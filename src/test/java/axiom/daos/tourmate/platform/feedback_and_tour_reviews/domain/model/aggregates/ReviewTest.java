
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    void shouldCreateReviewWithValidData() {
        Review review = new Review(1L, 3L, 5);

        assertNull(review.getId());
        assertEquals(Long.valueOf(1), review.getUserId());
        assertEquals(Long.valueOf(3), review.getTourId());
        assertEquals(Integer.valueOf(5), review.getRating());
        assertNotNull(review.getCreatedAt());
    }

    @Test
    void shouldAcceptZeroStars() {
        Review review = new Review(1L, 3L, 0);

        assertEquals(Integer.valueOf(0), review.getRating());
    }

    @Test
    void shouldRejectInvalidRatings() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Review(1L, 3L, -1)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Review(1L, 3L, 6)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Review(1L, 3L, null)
        );
    }

    @Test
    void shouldRejectInvalidIdentifiers() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Review(null, 3L, 4)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Review(0L, 3L, 4)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Review(1L, -1L, 4)
        );
    }

    @Test
    void shouldUpdateOnlyRating() {
        Review review = new Review(1L, 3L, 5);
        Instant originalDate = review.getCreatedAt();

        Review updated = review.updateRating(0);

        assertSame(review, updated);
        assertEquals(Integer.valueOf(0), review.getRating());
        assertEquals(Long.valueOf(1), review.getUserId());
        assertEquals(Long.valueOf(3), review.getTourId());
        assertEquals(originalDate, review.getCreatedAt());
    }

    @Test
    void shouldKeepRatingWhenUpdateIsInvalid() {
        Review review = new Review(1L, 3L, 4);

        assertThrows(
                IllegalArgumentException.class,
                () -> review.updateRating(7)
        );

        assertEquals(Integer.valueOf(4), review.getRating());
    }

    @Test
    void shouldRestoreOriginalIdAndDate() {
        Instant createdAt = Instant.parse(
                "2026-11-01T10:00:00Z"
        );

        Review review = Review.reconstitute(
                9L, 1L, 3L, 0, createdAt
        );

        assertEquals(Long.valueOf(9), review.getId());
        assertEquals(Long.valueOf(1), review.getUserId());
        assertEquals(Long.valueOf(3), review.getTourId());
        assertEquals(Integer.valueOf(0), review.getRating());
        assertEquals(createdAt, review.getCreatedAt());
    }

    @Test
    void shouldRejectInvalidReconstitution() {
        Instant createdAt = Instant.now();

        assertThrows(
                IllegalArgumentException.class,
                () -> Review.reconstitute(
                        0L, 1L, 3L, 4, createdAt
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> Review.reconstitute(
                        9L, 1L, 3L, 4, null
                )
        );
    }
}
