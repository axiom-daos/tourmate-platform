
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.commandservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteCommentCommand;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;

/**
 * Define las operaciones de escritura de comentarios.
 */
public interface CommentCommandService {

    // Crear un comentario para una reseña.
    Result<Comment, ApplicationError> handle(
            CreateCommentCommand command
    );

    // Actualizar el contenido de un comentario.
    Result<Comment, ApplicationError> handle(
            UpdateCommentCommand command
    );

    // Eliminar un comentario.
    Result<Boolean, ApplicationError> handle(
            DeleteCommentCommand command
    );
}
