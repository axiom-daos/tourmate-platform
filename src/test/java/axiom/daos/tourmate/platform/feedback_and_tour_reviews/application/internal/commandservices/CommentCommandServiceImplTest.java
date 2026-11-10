package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.internal.commandservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.CommentRepository;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.ReviewRepository;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentCommandServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ReviewRepository reviewRepository;

    private CommentCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CommentCommandServiceImpl(
                commentRepository,
                reviewRepository
        );
    }

    private Review existingReview() {
        return Review.reconstitute(
                9L, 1L, 3L, 5, Instant.now()
        );
    }

    private Comment existingComment() {
        return Comment.reconstitute(
                7L, 9L, "Buen recorrido", Instant.now()
        );
    }

    @Test
    void shouldCreateCommentSuccessfully() {
        when(reviewRepository.findById(9L))
                .thenReturn(Optional.of(existingReview()));

        when(commentRepository.findByReviewId(9L))
                .thenReturn(List.of());

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        var result = service.handle(
                new CreateCommentCommand(
                        9L, "Excelente recorrido"
                )
        );

        assertTrue(result.isSuccess());

        Comment created = result.toOptional().orElseThrow();

        assertEquals(Long.valueOf(9), created.getReviewId());
        assertEquals("Excelente recorrido", created.getContent());

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void shouldRejectCommentForMissingReview() {
        when(reviewRepository.findById(99L))
                .thenReturn(Optional.empty());

        var result = service.handle(
                new CreateCommentCommand(
                        99L, "Buen recorrido"
                )
        );

        assertEquals("REVIEW_NOT_FOUND", getError(result).code());

        verify(commentRepository, never())
                .save(any(Comment.class));
    }

    @Test
    void shouldRejectDuplicateComment() {
        when(reviewRepository.findById(9L))
                .thenReturn(Optional.of(existingReview()));

        when(commentRepository.findByReviewId(9L))
                .thenReturn(List.of(existingComment()));

        var result = service.handle(
                new CreateCommentCommand(
                        9L, "Otro comentario"
                )
        );

        assertEquals("COMMENT_CONFLICT", getError(result).code());

        verify(commentRepository, never())
                .save(any(Comment.class));
    }

    @Test
    void shouldUpdateOnlyCommentContent() {
        Comment comment = existingComment();
        Instant originalDate = comment.getCreatedAt();

        when(commentRepository.findById(7L))
                .thenReturn(Optional.of(comment));

        when(reviewRepository.existsById(9L))
                .thenReturn(true);

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        var result = service.handle(
                new UpdateCommentCommand(
                        7L, 9L, "Excelente experiencia"
                )
        );

        assertTrue(result.isSuccess());

        Comment updated = result.toOptional().orElseThrow();

        assertEquals("Excelente experiencia", updated.getContent());
        assertEquals(Long.valueOf(7), updated.getId());
        assertEquals(Long.valueOf(9), updated.getReviewId());
        assertEquals(originalDate, updated.getCreatedAt());
    }

    @Test
    void shouldRejectChangingReviewId() {
        Comment comment = existingComment();

        when(commentRepository.findById(7L))
                .thenReturn(Optional.of(comment));

        var result = service.handle(
                new UpdateCommentCommand(
                        7L, 10L, "Nuevo contenido"
                )
        );

        assertEquals(
                "BUSINESS_RULE_VIOLATION",
                getError(result).code()
        );

        assertEquals(Long.valueOf(9), comment.getReviewId());

        verify(commentRepository, never())
                .save(any(Comment.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingComment() {
        when(commentRepository.findById(99L))
                .thenReturn(Optional.empty());

        var result = service.handle(
                new UpdateCommentCommand(
                        99L, 9L, "Nuevo contenido"
                )
        );

        assertEquals("COMMENT_NOT_FOUND", getError(result).code());
    }

    @Test
    void shouldDeleteExistingComment() {
        when(commentRepository.findById(7L))
                .thenReturn(Optional.of(existingComment()));

        when(reviewRepository.existsById(9L))
                .thenReturn(true);

        var result = service.handle(
                new DeleteCommentCommand(7L)
        );

        assertTrue(result.isSuccess());
        assertEquals(Boolean.TRUE, result.toOptional().orElseThrow());

        verify(commentRepository).deleteById(7L);
    }

    @Test
    void shouldRejectDeletingMissingComment() {
        when(commentRepository.findById(99L))
                .thenReturn(Optional.empty());

        var result = service.handle(
                new DeleteCommentCommand(99L)
        );

        assertEquals("COMMENT_NOT_FOUND", getError(result).code());

        verify(commentRepository, never())
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
