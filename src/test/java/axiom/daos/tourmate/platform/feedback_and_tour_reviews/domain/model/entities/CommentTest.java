
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void shouldCreateCommentWithValidData() {
        Comment comment = new Comment(
                9L, "Excelente recorrido"
        );

        assertNull(comment.getId());
        assertEquals(Long.valueOf(9), comment.getReviewId());
        assertEquals("Excelente recorrido", comment.getContent());
        assertNotNull(comment.getCreatedAt());
    }

    @Test
    void shouldTrimCommentContent() {
        Comment comment = new Comment(
                9L, "  Muy buen tour  "
        );

        assertEquals("Muy buen tour", comment.getContent());
    }

    @Test
    void shouldRejectInvalidReviewId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Comment(null, "Buen recorrido")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Comment(0L, "Buen recorrido")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Comment(-1L, "Buen recorrido")
        );
    }

    @Test
    void shouldRejectShortOrEmptyContent() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Comment(9L, "Hola")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Comment(9L, "     ")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Comment(9L, null)
        );
    }

    @Test
    void shouldUpdateOnlyContent() {
        Comment comment = new Comment(
                9L, "Buen recorrido"
        );

        Instant originalDate = comment.getCreatedAt();

        Comment updated = comment.updateContent(
                "  Excelente experiencia  "
        );

        assertSame(comment, updated);
        assertEquals(
                "Excelente experiencia",
                comment.getContent()
        );
        assertEquals(Long.valueOf(9), comment.getReviewId());
        assertEquals(originalDate, comment.getCreatedAt());
    }

    @Test
    void shouldKeepContentWhenUpdateIsInvalid() {
        Comment comment = new Comment(
                9L, "Buen recorrido"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> comment.updateContent("Mal")
        );

        assertEquals("Buen recorrido", comment.getContent());
    }

    @Test
    void shouldRestoreOriginalIdAndDate() {
        Instant createdAt = Instant.parse(
                "2026-11-01T12:00:00Z"
        );

        Comment comment = Comment.reconstitute(
                5L, 9L, "Muy buena experiencia", createdAt
        );

        assertEquals(Long.valueOf(5), comment.getId());
        assertEquals(Long.valueOf(9), comment.getReviewId());
        assertEquals(
                "Muy buena experiencia",
                comment.getContent()
        );
        assertEquals(createdAt, comment.getCreatedAt());
    }

    @Test
    void shouldRejectInvalidReconstitution() {
        Instant createdAt = Instant.now();

        assertThrows(
                IllegalArgumentException.class,
                () -> Comment.reconstitute(
                        -1L, 9L, "Buen recorrido", createdAt
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> Comment.reconstitute(
                        5L, 9L, "Buen recorrido", null
                )
        );
    }
}
