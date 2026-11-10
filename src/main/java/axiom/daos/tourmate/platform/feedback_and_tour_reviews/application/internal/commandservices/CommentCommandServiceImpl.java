
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.internal.commandservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.commandservices.CommentCommandService;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.internal.authorization.ReviewOwnershipAuthorizer;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateCommentCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteCommentCommand;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.CommentRepository;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.ReviewRepository;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementación del servicio de comandos de comentarios.
 *
 * Gestiona la creación, actualización y eliminación
 * de comentarios asociados a reseñas turísticas.
 *
 * Reglas principales:
 * - La reseña debe existir.
 * - Solo su propietario puede gestionar el comentario.
 * - Cada reseña puede tener como máximo un comentario.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@Service
@Transactional
public class CommentCommandServiceImpl implements CommentCommandService {

    // Repositorio de comentarios.
    private final CommentRepository commentRepository;

    // Repositorio de reseñas.
    private final ReviewRepository reviewRepository;

    // Servicio para comprobar la identidad del propietario.
    private final ReviewOwnershipAuthorizer ownershipAuthorizer;

    /**
     * Constructor con inyección de dependencias.
     */
    public CommentCommandServiceImpl(
            CommentRepository commentRepository,
            ReviewRepository reviewRepository,
            ReviewOwnershipAuthorizer ownershipAuthorizer
    ) {
        this.commentRepository = commentRepository;
        this.reviewRepository = reviewRepository;
        this.ownershipAuthorizer = ownershipAuthorizer;
    }

    /**
     * Crea un comentario asociado a una reseña.
     *
     * Primero comprueba que la reseña exista,
     * que el usuario sea su propietario y que
     * todavía no tenga un comentario registrado.
     *
     * @param command Datos del comentario.
     * @return Comentario creado o error de aplicación.
     */
    @Override
    public Result<Comment, ApplicationError> handle(
            CreateCommentCommand command
    ) {

        // Buscamos la reseña a la que pertenece el comentario.
        Optional<Review> reviewOptional =
                reviewRepository.findById(
                        command.reviewId()
                );

        // No permitimos comentar reseñas inexistentes.
        if (reviewOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound(
                            "Review",
                            command.reviewId().toString()
                    )
            );
        }

        // Recuperamos la reseña original.
        Review review = reviewOptional.get();

        // Verificamos que el usuario autenticado
        // sea el propietario de la reseña.
        ownershipAuthorizer.requireOwner(
                review.getUserId()
        );

        // REGLA DE NEGOCIO:
        // Cada reseña puede tener como máximo un comentario.
        boolean alreadyHasComment =
                !commentRepository.findByReviewId(
                        command.reviewId()
                ).isEmpty();

        // Si ya existe un comentario, rechazamos
        // la creación para evitar duplicados.
        if (alreadyHasComment) {
            return Result.failure(
                    ApplicationError.conflict(
                            "Comment",
                            "Esta reseña ya tiene un comentario registrado."
                    )
            );
        }

        // Creamos el comentario.
        // Su constructor valida el contenido.
        Comment comment = new Comment(
                command.reviewId(),
                command.content()
        );

        // Guardamos el nuevo comentario.
        Comment savedComment =
                commentRepository.save(comment);

        // Devolvemos el comentario registrado.
        return Result.success(savedComment);
    }

    /**
     * Actualiza un comentario existente.
     *
     * Comprueba que el comentario exista,
     * pertenezca a la reseña indicada
     * y que el usuario sea su propietario.
     *
     * @param command Datos de actualización.
     * @return Comentario actualizado o error.
     */
    @Override
    public Result<Comment, ApplicationError> handle(
            UpdateCommentCommand command
    ) {

        // Buscamos el comentario que se desea editar.
        Optional<Comment> commentOptional =
                commentRepository.findById(
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

        // Recuperamos el comentario.
        Comment comment = commentOptional.get();

        // El comentario debe conservar
        // su relación original con la reseña.
        if (!comment.getReviewId().equals(
                command.reviewId()
        )) {

            return Result.failure(
                    ApplicationError.businessRuleViolation(
                            "Comment review",
                            "El comentario no pertenece "
                                    + "a la reseña indicada."
                    )
            );
        }

        // Buscamos la reseña relacionada.
        Optional<Review> reviewOptional =
                reviewRepository.findById(
                        comment.getReviewId()
                );

        // Verificamos que la reseña exista.
        if (reviewOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound(
                            "Review",
                            comment.getReviewId().toString()
                    )
            );
        }

        // Solo el propietario puede editar.
        ownershipAuthorizer.requireOwner(
                reviewOptional.get().getUserId()
        );

        // Actualizamos únicamente el contenido.
        // Conservamos el ID y el reviewId originales.
        comment.updateContent(
                command.content()
        );

        // Guardamos los cambios.
        Comment updatedComment =
                commentRepository.save(comment);

        return Result.success(updatedComment);
    }

    /**
     * Elimina un comentario existente.
     *
     * Comprueba que el comentario y su reseña
     * existan, y que el usuario tenga autorización.
     *
     * @param command Identificador del comentario.
     * @return true si se eliminó o error.
     */
    @Override
    public Result<Boolean, ApplicationError> handle(
            DeleteCommentCommand command
    ) {

        // Buscamos el comentario solicitado.
        Optional<Comment> commentOptional =
                commentRepository.findById(
                        command.commentId()
                );

        // Comprobamos que exista.
        if (commentOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound(
                            "Comment",
                            command.commentId().toString()
                    )
            );
        }

        // Recuperamos el comentario original.
        Comment comment = commentOptional.get();

        // Buscamos la reseña relacionada.
        Optional<Review> reviewOptional =
                reviewRepository.findById(
                        comment.getReviewId()
                );

        // Rechazamos la operación si la reseña no existe.
        if (reviewOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound(
                            "Review",
                            comment.getReviewId().toString()
                    )
            );
        }

        // Comprobamos la identidad del propietario.
        ownershipAuthorizer.requireOwner(
                reviewOptional.get().getUserId()
        );

        // Eliminamos el comentario.
        commentRepository.deleteById(
                command.commentId()
        );

        return Result.success(true);
    }
}
