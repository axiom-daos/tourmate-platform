
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.commandservices.CommentCommandService;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.queryservices.CommentQueryService;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetAllCommentsQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetCommentByIdQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetCommentsByReviewIdQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources.CommentResource;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources.CreateCommentResource;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentsControllerTest {

    @Mock
    private CommentCommandService commandService;

    @Mock
    private CommentQueryService queryService;

    private CommentsController controller;

    @BeforeEach
    void setUp() {
        controller = new CommentsController(
                commandService,
                queryService
        );
    }

    private Comment existingComment() {
        return Comment.reconstitute(
                7L, 9L, "Excelente recorrido", Instant.now()
        );
    }

    @Test
    void shouldListAllComments() {
        when(queryService.handle(any(GetAllCommentsQuery.class)))
                .thenReturn(List.of(existingComment()));

        ResponseEntity<?> response = controller.getComments(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<?> body = assertInstanceOf(
                List.class, response.getBody()
        );

        assertEquals(1, body.size());

        CommentResource resource = assertInstanceOf(
                CommentResource.class, body.get(0)
        );

        assertEquals(Long.valueOf(7), resource.id());
        assertEquals(Long.valueOf(9), resource.reviewId());
    }

    @Test
    void shouldFilterCommentsByReview() {
        when(queryService.handle(any(GetCommentsByReviewIdQuery.class)))
                .thenReturn(List.of(existingComment()));

        ResponseEntity<?> response = controller.getComments(9L);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<?> body = assertInstanceOf(
                List.class, response.getBody()
        );

        assertEquals(1, body.size());

        verify(queryService)
                .handle(any(GetCommentsByReviewIdQuery.class));
    }

    @Test
    void shouldFindCommentById() {
        when(queryService.handle(any(GetCommentByIdQuery.class)))
                .thenReturn(Optional.of(existingComment()));

        ResponseEntity<?> response = controller.getCommentById(7L);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        CommentResource resource = assertInstanceOf(
                CommentResource.class, response.getBody()
        );

        assertEquals(Long.valueOf(7), resource.id());
        assertEquals(Long.valueOf(9), resource.reviewId());
        assertEquals(
                "Excelente recorrido",
                resource.content()
        );
    }

    @Test
    void shouldReturnNotFoundForMissingComment() {
        when(queryService.handle(any(GetCommentByIdQuery.class)))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getCommentById(99L);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );
    }

    @Test
    void shouldCreateComment() {
        when(commandService.handle(any(CreateCommentCommand.class)))
                .thenReturn(Result.success(existingComment()));

        ResponseEntity<?> response = controller.createComment(
                new CreateCommentResource(
                        9L, "Excelente recorrido"
                )
        );

        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );

        CommentResource resource = assertInstanceOf(
                CommentResource.class, response.getBody()
        );

        assertEquals(Long.valueOf(7), resource.id());
        assertEquals(Long.valueOf(9), resource.reviewId());
    }

    @Test
    void shouldRejectShortComment() {
        ResponseEntity<?> response = controller.createComment(
                new CreateCommentResource(9L, "Mal")
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertInstanceOf(
                ProblemDetail.class,
                response.getBody()
        );

        verifyNoInteractions(commandService);
    }

    @Test
    void shouldUpdateCommentContent() {
        Comment comment = existingComment();
        comment.updateContent("Muy buena experiencia");

        when(commandService.handle(any(UpdateCommentCommand.class)))
                .thenReturn(Result.success(comment));

        ResponseEntity<?> response = controller.updateComment(
                7L,
                new CreateCommentResource(
                        9L, "Muy buena experiencia"
                )
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        CommentResource resource = assertInstanceOf(
                CommentResource.class, response.getBody()
        );

        assertEquals(
                "Muy buena experiencia",
                resource.content()
        );

        assertEquals(Long.valueOf(9), resource.reviewId());
    }

    @Test
    void shouldRejectDuplicateComment() {
        when(commandService.handle(any(CreateCommentCommand.class)))
                .thenReturn(
                        Result.failure(
                                ApplicationError.conflict(
                                        "Comment",
                                        "La reseña ya tiene un comentario."
                                )
                        )
                );

        ResponseEntity<?> response = controller.createComment(
                new CreateCommentResource(
                        9L, "Otro comentario"
                )
        );

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        assertInstanceOf(
                ProblemDetail.class,
                response.getBody()
        );
    }

    @Test
    void shouldDeleteComment() {
        when(commandService.handle(any(DeleteCommentCommand.class)))
                .thenReturn(Result.success(true));

        ResponseEntity<?> response = controller.deleteComment(7L);

        assertEquals(
                HttpStatus.NO_CONTENT,
                response.getStatusCode()
        );

        verify(commandService)
                .handle(any(DeleteCommentCommand.class));
    }
}
