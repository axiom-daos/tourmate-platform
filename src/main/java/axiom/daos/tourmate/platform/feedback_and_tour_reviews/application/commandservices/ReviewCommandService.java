
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.commandservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteReviewCommand;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;

/**
 * Define las operaciones de escritura de reseñas.
 */
public interface ReviewCommandService {

    // Crear una reseña.
    Result<Review, ApplicationError> handle(
            CreateReviewCommand command
    );

    // Actualizar la calificación de una reseña.
    Result<Review, ApplicationError> handle(
            UpdateReviewCommand command
    );

    // Eliminar una reseña y sus comentarios.
    Result<Boolean, ApplicationError> handle(
            DeleteReviewCommand command
    );
}
