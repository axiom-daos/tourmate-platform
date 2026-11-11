
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.commandservices.CommentCommandService;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.queryservices.CommentQueryService;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteCommentCommand;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetAllCommentsQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetCommentByIdQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetCommentsByReviewIdQuery;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources.CreateCommentResource;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources.CommentResource;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

/**
 * Controlador REST para gestionar comentarios.
 */
@RestController
@RequestMapping(
        value = "/api/v1/comments",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(
        name = "Comments",
        description = "Tour review comment management endpoints"
)
public class CommentsController {

    private final CommentCommandService commandService;
    private final CommentQueryService queryService;

    public CommentsController(
            CommentCommandService commandService,
            CommentQueryService queryService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    /**
     * Obtiene todos los comentarios o filtra por reseña.
     */
    @GetMapping
    @Operation(summary = "Get all comments")
    public ResponseEntity<?> getComments(
            @RequestParam(required = false) Long reviewId
    ) {

        return executeSafely(() -> {

            var comments = reviewId == null
                    ? queryService.handle(new GetAllCommentsQuery())
                    : queryService.handle(
                    new GetCommentsByReviewIdQuery(reviewId)
            );

            var resources = comments.stream()
                    .map(CommentsController::toResource)
                    .toList();

            return ResponseEntity.ok(resources);
        });
    }

    /**
     * Busca un comentario por su identificador.
     */
    @GetMapping("/{commentId}")
    @Operation(summary = "Get comment by ID")
    public ResponseEntity<?> getCommentById(
            @PathVariable Long commentId
    ) {

        return executeSafely(() -> {

            var comment = queryService.handle(
                    new GetCommentByIdQuery(commentId)
            );

            if (comment.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(
                    toResource(comment.get())
            );
        });
    }

    /**
     * Crea un comentario para una reseña.
     */
    @PostMapping
    @Operation(summary = "Create a new comment")
    public ResponseEntity<?> createComment(
            @RequestBody CreateCommentResource resource
    ) {

        return executeSafely(() -> {

            CreateCommentCommand command = new CreateCommentCommand(
                    resource.reviewId(),
                    resource.content()
            );

            var result = commandService.handle(command);

            return ResponseEntityAssembler
                    .toResponseEntityFromResult(
                            result,
                            CommentsController::toResource,
                            HttpStatus.CREATED
                    );
        });
    }

    /**
     * Actualiza solamente el contenido del comentario.
     */
    @PutMapping("/{commentId}")
    @Operation(summary = "Update a comment")
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @RequestBody CreateCommentResource resource
    ) {

        return executeSafely(() -> {

            UpdateCommentCommand command = new UpdateCommentCommand(
                    commentId,
                    resource.reviewId(),
                    resource.content()
            );

            var result = commandService.handle(command);

            return ResponseEntityAssembler
                    .toResponseEntityFromResult(
                            result,
                            CommentsController::toResource,
                            HttpStatus.OK
                    );
        });
    }

    /**
     * Elimina un comentario existente.
     */
    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete a comment")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId
    ) {

        return executeSafely(() -> {

            var result = commandService.handle(
                    new DeleteCommentCommand(commentId)
            );

            if (result instanceof Result.Failure<Boolean, ApplicationError> failure) {
                return ProblemDetailFromApplicationErrorAssembler
                        .toResponseFromApplicationError(failure.error());
            }

            return ResponseEntity.noContent().build();
        });
    }

    /**
     * Convierte un comentario en un recurso REST.
     */
    private static CommentResource toResource(
            Comment comment
    ) {

        return new CommentResource(
                comment.getId(),
                comment.getReviewId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

    /**
     * Convierte validaciones incorrectas en HTTP 400.
     */
    private ResponseEntity<?> executeSafely(
            Supplier<ResponseEntity<?>> operation
    ) {

        try {
            return operation.get();

        } catch (IllegalArgumentException exception) {

            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );

            return ResponseEntity.badRequest().body(problem);
        }
    }
}
