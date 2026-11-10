
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.internal.commandservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.ReviewRepository;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.CommentRepository;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewCommandServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private CommentRepository commentRepository;

    private ReviewCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ReviewCommandServiceImpl(
                reviewRepository,
                commentRepository
        );
    }

    @Test
    void shouldCreateReviewSuccessfully() {
        Review saved = Review.reconstitute(
                9L, 1L, 3L, 0, Instant.now()
        );

        when(reviewRepository.save(any(Review.class)))
                .thenReturn(saved);

        var result = service.handle(
                new CreateReviewCommand(1L, 3L, 0)
        );

        assertTrue(result.isSuccess());

        Review created = result.toOptional().orElseThrow();

        assertEquals(Long.valueOf(9), created.getId());
        assertEquals(Integer.valueOf(0), created.getRating());

        verify(reviewRepository)
                .existsByUserIdAndTourId(1L, 3L);

        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void shouldRejectDuplicateReview() {
        when(reviewRepository.existsByUserIdAndTourId(1L, 3L))
                .thenReturn(true);

        var result = service.handle(
                new CreateReviewCommand(1L, 3L, 5)
        );

        assertEquals("REVIEW_CONFLICT", getError(result).code());

        verify(reviewRepository, never())
                .save(any(Review.class));
    }

    @Test
    void shouldHandleDatabaseDuplicateConflict() {
        when(reviewRepository.save(any(Review.class)))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "duplicate key violates constraint " +
                                        "uk_reviews_user_tour"
                        )
                );

        var result = service.handle(
                new CreateReviewCommand(1L, 3L, 4)
        );

        assertEquals("REVIEW_CONFLICT", getError(result).code());
    }

    @Test
    void shouldHandleOtherDatabaseErrors() {
        when(reviewRepository.save(any(Review.class)))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "Error de integridad diferente"
                        )
                );

        var result = service.handle(
                new CreateReviewCommand(1L, 3L, 4)
        );

        assertEquals("UNEXPECTED_ERROR", getError(result).code());
    }

    @Test
    void shouldUpdateOnlyRating() {
        Instant originalDate = Instant.parse(
                "2026-11-01T10:00:00Z"
        );

        Review review = Review.reconstitute(
                9L, 1L, 3L, 5, originalDate
        );

        when(reviewRepository.findById(9L))
                .thenReturn(Optional.of(review));

        when(reviewRepository.save(any(Review.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        var result = service.handle(
                new UpdateReviewCommand(9L, 1L, 3L, 0)
        );

        assertTrue(result.isSuccess());

        Review updated = result.toOptional().orElseThrow();

        assertEquals(Integer.valueOf(0), updated.getRating());
        assertEquals(Long.valueOf(1), updated.getUserId());
        assertEquals(Long.valueOf(3), updated.getTourId());
        assertEquals(originalDate, updated.getCreatedAt());

        verify(reviewRepository).save(review);
    }

    @Test
    void shouldRejectChangingUserOrTour() {
        Review review = Review.reconstitute(
                9L, 1L, 3L, 5, Instant.now()
        );

        when(reviewRepository.findById(9L))
                .thenReturn(Optional.of(review));

        var changeUser = service.handle(
                new UpdateReviewCommand(9L, 2L, 3L, 4)
        );

        var changeTour = service.handle(
                new UpdateReviewCommand(9L, 1L, 4L, 4)
        );

        assertEquals(
                "BUSINESS_RULE_VIOLATION",
                getError(changeUser).code()
        );

        assertEquals(
                "BUSINESS_RULE_VIOLATION",
                getError(changeTour).code()
        );

        assertEquals(Integer.valueOf(5), review.getRating());

        verify(reviewRepository, never())
                .save(any(Review.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingReview() {
        when(reviewRepository.findById(99L))
                .thenReturn(Optional.empty());

        var result = service.handle(
                new UpdateReviewCommand(99L, 1L, 3L, 4)
        );

        assertEquals("REVIEW_NOT_FOUND", getError(result).code());
    }

    @Test
    void shouldDeleteCommentsBeforeReview() {
        when(reviewRepository.existsById(9L))
                .thenReturn(true);

        var result = service.handle(
                new DeleteReviewCommand(9L)
        );

        assertTrue(result.isSuccess());
        assertEquals(Boolean.TRUE, result.toOptional().orElseThrow());

        InOrder order = inOrder(
                commentRepository,
                reviewRepository
        );

        order.verify(commentRepository).deleteByReviewId(9L);
        order.verify(reviewRepository).deleteById(9L);
    }

    @Test
    void shouldRejectDeletingMissingReview() {
        when(reviewRepository.existsById(99L))
                .thenReturn(false);

        var result = service.handle(
                new DeleteReviewCommand(99L)
        );

        assertEquals("REVIEW_NOT_FOUND", getError(result).code());

        verify(commentRepository, never())
                .deleteByReviewId(any());

        verify(reviewRepository, never())
                .deleteById(any());
    }

    private static <T> ApplicationError getError(
            Result<T, ApplicationError> result
    ) {
        assertInstanceOf(Result.Failure.class, result);

        Result.Failure<T, ApplicationError> failure =
                (Result.Failure<T, ApplicationError>) result;

        return failure.error();
    }
}
