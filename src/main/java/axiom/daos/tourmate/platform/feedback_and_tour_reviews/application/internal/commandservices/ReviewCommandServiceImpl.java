
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.internal.commandservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.commandservices.ReviewCommandService;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.ReviewRepository;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.CommentRepository;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Locale;
import java.util.Optional;

/**
 * Servicio de comandos para gestionar reseñas.
 */
@Service
public class ReviewCommandServiceImpl implements ReviewCommandService {

    private static final String UNIQUE_REVIEW_CONSTRAINT =
            "uk_reviews_user_tour";

    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;

    public ReviewCommandServiceImpl(
            ReviewRepository reviewRepository,
            CommentRepository commentRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Crea una reseña si el usuario todavía no calificó el tour.
     */
    @Override
    public Result<Review, ApplicationError> handle(
            CreateReviewCommand command
    ) {

        try {
            boolean alreadyReviewed =
                    reviewRepository.existsByUserIdAndTourId(
                            command.userId(),
                            command.tourId()
                    );

            if (alreadyReviewed) {
                return Result.failure(
                        ApplicationError.conflict(
                                "Review",
                                "El usuario ya registró una reseña para este tour."
                        )
                );
            }

            Review review = new Review(
                    command.userId(),
                    command.tourId(),
                    command.rating()
            );

            Review savedReview = reviewRepository.save(review);

            return Result.success(savedReview);

        } catch (IllegalArgumentException exception) {

            return Result.failure(
                    ApplicationError.validationError(
                            "Review",
                            exception.getMessage()
                    )
            );

        } catch (DataIntegrityViolationException exception) {

            // PostgreSQL también impide reseñas duplicadas.
            if (hasConstraint(exception, UNIQUE_REVIEW_CONSTRAINT)) {
                return Result.failure(
                        ApplicationError.conflict(
                                "Review",
                                "El usuario ya registró una reseña para este tour."
                        )
                );
            }

            return Result.failure(
                    ApplicationError.unexpected(
                            "create-review",
                            "No se pudo guardar la reseña."
                    )
            );

        } catch (RuntimeException exception) {

            return Result.failure(
                    ApplicationError.unexpected(
                            "create-review",
                            "Ocurrió un error al crear la reseña."
                    )
            );
        }
    }

    /**
     * Actualiza únicamente la calificación de una reseña.
     */
    @Override
    @Transactional
    public Result<Review, ApplicationError> handle(
            UpdateReviewCommand command
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

            Review review = reviewOptional.get();

            boolean sameUser =
                    review.getUserId().equals(command.userId());

            boolean sameTour =
                    review.getTourId().equals(command.tourId());

            // El usuario y el tour originales no se modifican.
            if (!sameUser || !sameTour) {
                return Result.failure(
                        ApplicationError.businessRuleViolation(
                                "Review association",
                                "No se puede cambiar el usuario ni el tour."
                        )
                );
            }

            review.updateRating(command.rating());

            Review updatedReview = reviewRepository.save(review);

            return Result.success(updatedReview);

        } catch (IllegalArgumentException exception) {

            return Result.failure(
                    ApplicationError.validationError(
                            "Review",
                            exception.getMessage()
                    )
            );

        } catch (RuntimeException exception) {

            TransactionAspectSupport.currentTransactionStatus()
                    .setRollbackOnly();

            return Result.failure(
                    ApplicationError.unexpected(
                            "update-review",
                            "No se pudo actualizar la reseña."
                    )
            );
        }
    }

    /**
     * Elimina una reseña y sus comentarios relacionados.
     */
    @Override
    @Transactional
    public Result<Boolean, ApplicationError> handle(
            DeleteReviewCommand command
    ) {

        try {
            boolean exists =
                    reviewRepository.existsById(command.reviewId());

            if (!exists) {
                return Result.failure(
                        ApplicationError.notFound(
                                "Review",
                                command.reviewId().toString()
                        )
                );
            }

            // Ambas eliminaciones pertenecen a una transacción.
            commentRepository.deleteByReviewId(command.reviewId());
            reviewRepository.deleteById(command.reviewId());

            return Result.success(true);

        } catch (RuntimeException exception) {

            TransactionAspectSupport.currentTransactionStatus()
                    .setRollbackOnly();

            return Result.failure(
                    ApplicationError.unexpected(
                            "delete-review",
                            "No se pudo eliminar la reseña."
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
