
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.commandservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteCommentCommand;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;

/**
 * Contrato del servicio de comandos de comentarios.
 *
 * Define las operaciones de escritura necesarias
 * para crear, actualizar y eliminar comentarios.
 *
 * Sigue el patrón CQRS utilizado en TourMate,
 * separando las escrituras de las consultas.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
public interface CommentCommandService {

    /**
     * Procesa la creación de un comentario.
     *
     * La implementación deberá verificar que
     * la reseña relacionada exista y validar
     * el contenido del comentario.
     *
     * @param command Datos del nuevo comentario.
     * @return Comentario creado o error de aplicación.
     */
    Result<Comment, ApplicationError> handle(
            CreateCommentCommand command
    );

    /**
     * Procesa la actualización de un comentario.
     *
     * La implementación comprobará que el comentario
     * exista y pertenezca a la reseña indicada.
     *
     * También deberá verificar los permisos
     * necesarios para realizar la modificación.
     *
     * @param command Datos del comentario actualizado.
     * @return Comentario actualizado o error.
     */
    Result<Comment, ApplicationError> handle(
            UpdateCommentCommand command
    );

    /**
     * Procesa la eliminación de un comentario.
     *
     * La implementación deberá comprobar que
     * el comentario exista y que la operación
     * esté autorizada.
     *
     * @param command Identificador del comentario.
     * @return true si se eliminó o error de aplicación.
     */
    Result<Boolean, ApplicationError> handle(
            DeleteCommentCommand command
    );
}
