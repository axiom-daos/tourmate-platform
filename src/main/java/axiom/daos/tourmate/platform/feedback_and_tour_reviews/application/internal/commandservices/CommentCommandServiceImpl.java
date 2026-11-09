
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.internal.commandservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.commandservices.CommentCommandService;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteCommentCommand;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.CommentRepository;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.ReviewRepository;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de comandos de comentarios.
 *
 * Procesa las operaciones de creación, actualización
 * y eliminación de comentarios turísticos.
 *
 * Utiliza los repositorios del dominio para mantener
 * la lógica de aplicación separada de JPA.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@Service
@Transactional
public class CommentCommandServiceImpl implements CommentCommandService {

    // Repositorio para gestionar los comentarios.
    private final CommentRepository commentRepository;

    // Repositorio para comprobar las reseñas relacionadas.
    private final ReviewRepository reviewRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * Spring proporcionará las implementaciones
     * de ambos repositorios.
     */
    public CommentCommandServiceImpl(
            CommentRepository commentRepository,
            ReviewRepository reviewRepository
    ) {
        this.commentRepository = commentRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * Procesa la creación de un nuevo comentario.
     *
     * Comprueba que la reseña relacionada exista
     * antes de registrar el comentario.
     *
     * @param command Datos del nuevo comentario.
     * @return Comentario creado o error de aplicación.
     */
    @Override
    public Result<Comment, ApplicationError> handle(
            CreateCommentCommand command
    ) {

        // Comprobamos que la reseña relacionada exista.
        boolean reviewExists = reviewRepository.existsById(
                command.reviewId()
        );

        // No permitimos crear comentarios
        // asociados a reseñas inexistentes.
        if (!reviewExists) {
            return Result.failure(
                    ApplicationError.notFound(
                            "Review",
                            command.reviewId().toString()
                    )
            );
        }

        // Creamos la entidad de dominio.
        // Su constructor valida el contenido.
        Comment comment = new Comment(
                command.reviewId(),
                command.content()
        );

        // Guardamos el comentario mediante el repositorio.
        Comment savedComment = commentRepository.save(comment);

        // Devolvemos el comentario registrado.
        return Result.success(savedComment);
    }

    /**
     * Procesa la actualización de un comentario.
     *
     * Comprueba que el comentario exista y
     * pertenezca a la reseña indicada.
     *
     * @param command Datos de actualización.
     * @return Comentario actualizado o error.
     */
    @Override
    public Result<Comment, ApplicationError> handle(
            UpdateCommentCommand command
    ) {

        // Buscamos el comentario que se desea editar.
        var commentOptional = commentRepository.findById(
                command.commentId()
        );

        // Verificamos que el comentario exista.
        if (commentOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound(
                            "Comment",
                            command.commentId().toString()
                    )
            );
        }

        // Recuperamos la entidad de dominio.
        Comment comment = commentOptional.get();

        // Comprobamos que el comentario pertenezca
        // a la reseña indicada en la solicitud.
        if (!comment.getReviewId().equals(command.reviewId())) {
            return Result.failure(
                    ApplicationError.businessRuleViolation(
                            "Comment review",
                            "El comentario no pertenece a la reseña indicada."
                    )
            );
        }

        // Actualizamos el texto del comentario.
        // El dominio valida su longitud mínima.
        comment.updateContent(command.content());

        // Guardamos los cambios en el repositorio.
        Comment updatedComment = commentRepository.save(comment);

        // Devolvemos el comentario actualizado.
        return Result.success(updatedComment);
    }

    /**
     * Procesa la eliminación de un comentario.
     *
     * Comprueba que exista antes de eliminarlo.
     *
     * @param command Identificador del comentario.
     * @return true si se eliminó o error de aplicación.
     */
    @Override
    public Result<Boolean, ApplicationError> handle(
            DeleteCommentCommand command
    ) {

        // Verificamos si el comentario existe.
        boolean exists = commentRepository.existsById(
                command.commentId()
        );

        // Si no existe, devolvemos un error.
        if (!exists) {
            return Result.failure(
                    ApplicationError.notFound(
                            "Comment",
                            command.commentId().toString()
                    )
            );
        }

        // Eliminamos el comentario solicitado.
        commentRepository.deleteById(
                command.commentId()
        );

        // Indicamos que la eliminación fue exitosa.
        return Result.success(true);
    }
}
