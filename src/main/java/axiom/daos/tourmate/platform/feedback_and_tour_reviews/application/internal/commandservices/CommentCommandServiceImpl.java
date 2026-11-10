
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.internal.commandservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.commandservices.CommentCommandService;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.CommentRepository;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.ReviewRepository;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

/**
 * Servicio de comandos para gestionar comentarios.
 */
@Service
public class CommentCommandServiceImpl implements CommentCommandService {

    private static final String UNIQUE_COMMENT_CONSTRAINT =
            "uk_comments_review_id";

    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    public CommentCommandServiceImpl(
            CommentRepository commentRepository,
            ReviewRepository reviewRepository
    ) {
        this.commentRepository = commentRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * Crea un comentario asociado a una reseña existente.
     */
    @Override
    public Result<Comment, ApplicationError> handle(
            CreateCommentCommand command
    ) {

        try {
            Optional<Review> reviewOptional =
                    reviewRepository.findById(command.reviewId());

            if (reviewOptional.isEmpty()) {
                return Result.failure(
                        ApplicationError.notFound(
                                "Review",
                                command.reviewId().toString()
                        )
                );
            }

            // Una reseña puede tener como máximo un comentario.
            boolean alreadyHasComment =
                    !commentRepository.findByReviewId(
                            command.reviewId()
                    ).isEmpty();

            if (alreadyHasComment) {
                return Result.failure(
                        ApplicationError.conflict(
                                "Comment",
                                "Esta reseña ya tiene un comentario."
                        )
                );
            }

            Comment comment = new Comment(
                    command.reviewId(),
                    command.content()
            );

            Comment savedComment = commentRepository.save(comment);

            return Result.success(savedComment);

        } catch (IllegalArgumentException exception) {

            return Result.failure(
                    ApplicationError.validationError(
                            "Comment",
                            exception.getMessage()
                    )
            );

        } catch (DataIntegrityViolationException exception) {

            // La restricción UNIQUE también evita duplicados.
            if (hasConstraint(exception, UNIQUE_COMMENT_CONSTRAINT)) {
                return Result.failure(
                        ApplicationError.conflict(
                                "Comment",
                                "Esta reseña ya tiene un comentario."
                        )
                );
            }

            return Result.failure(
                    ApplicationError.unexpected(
                            "create-comment",
                            "No se pudo guardar el comentario."
                    )
            );

        } catch (RuntimeException exception) {

            return Result.failure(
                    ApplicationError.unexpected(
                            "create-comment",
                            "Ocurrió un error al crear el comentario."
                    )
            );
        }
    }

    /**
     * Actualiza solamente el contenido del comentario.
     */
    @Override
    public Result<Comment, ApplicationError> handle(
            UpdateCommentCommand command
    ) {

        try {
            Optional<Comment> commentOptional =
                    commentRepository.findById(command.commentId());

            if (commentOptional.isEmpty()) {
                return Result.failure(
                        ApplicationError.notFound(
                                "Comment",
                                command.commentId().toString()
                        )
                );
            }

            Comment comment = commentOptional.get();

            // El comentario debe mantener su reviewId original.
            if (!comment.getReviewId().equals(command.reviewId())) {
                return Result.failure(
                        ApplicationError.businessRuleViolation(
                                "Comment review",
                                "No se puede cambiar la reseña del comentario."
                        )
                );
            }

            boolean reviewExists =
                    reviewRepository.existsById(comment.getReviewId());

            if (!reviewExists) {
                return Result.failure(
                        ApplicationError.notFound(
                                "Review",
                                comment.getReviewId().toString()
                        )
                );
            }

            comment.updateContent(command.content());

            Comment updatedComment =
                    commentRepository.save(comment);

            return Result.success(updatedComment);

        } catch (IllegalArgumentException exception) {

            return Result.failure(
                    ApplicationError.validationError(
                            "Comment",
                            exception.getMessage()
                    )
            );

        } catch (RuntimeException exception) {

            return Result.failure(
                    ApplicationError.unexpected(
                            "update-comment",
                            "No se pudo actualizar el comentario."
                    )
            );
        }
    }

    /**
     * Elimina un comentario existente.
     */
    @Override
    public Result<Boolean, ApplicationError> handle(
            DeleteCommentCommand command
    ) {

        try {
            Optional<Comment> commentOptional =
                    commentRepository.findById(command.commentId());

            if (commentOptional.isEmpty()) {
                return Result.failure(
                        ApplicationError.notFound(
                                "Comment",
                                command.commentId().toString()
                        )
                );
            }

            Comment comment = commentOptional.get();

            boolean reviewExists =
                    reviewRepository.existsById(comment.getReviewId());

            if (!reviewExists) {
                return Result.failure(
                        ApplicationError.notFound(
                                "Review",
                                comment.getReviewId().toString()
                        )
                );
            }

            commentRepository.deleteById(command.commentId());

            return Result.success(true);

        } catch (RuntimeException exception) {

            return Result.failure(
                    ApplicationError.unexpected(
                            "delete-comment",
                            "No se pudo eliminar el comentario."
                    )
            );
        }
    }

    /**
     * Identifica una restricción específica de PostgreSQL.
     */
    private boolean hasConstraint(
            Throwable exception,
            String constraintName
    ) {

        Throwable current = exception;

        while (current != null) {
            String message = current.getMessage();

            if (message != null &&
                    message.toLowerCase(Locale.ROOT)
                            .contains(constraintName)) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }
}
