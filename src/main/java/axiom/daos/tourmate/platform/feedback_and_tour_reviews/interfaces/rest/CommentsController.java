
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

import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Controlador REST para gestionar comentarios
 * asociados a las reseñas turísticas.
 *
 * Expone las operaciones CRUD mediante HTTP.
 *
 * Delega las operaciones de lectura y escritura
 * a los servicios de aplicación correspondientes.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@RestController
@RequestMapping(
        value = "/api/v1/comments",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(
        name = "Comments",
        description = "Gestión de comentarios de reseñas turísticas"
)
public class CommentsController {

    // Servicio encargado de crear, actualizar
    // y eliminar comentarios.
    private final CommentCommandService commandService;

    // Servicio encargado de consultar comentarios.
    private final CommentQueryService queryService;

    /**
     * Constructor con inyección de dependencias.
     *
     * Spring proporciona automáticamente
     * las implementaciones de ambos servicios.
     */
    public CommentsController(
            CommentCommandService commandService,
            CommentQueryService queryService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    /**
     * Obtiene todos los comentarios registrados
     * o filtra por una reseña específica.
     *
     * GET /api/v1/comments
     * GET /api/v1/comments?reviewId=15
     *
     * @param reviewId Identificador opcional de la reseña.
     * @return Lista de comentarios.
     */
    @GetMapping
    @Operation(summary = "Consultar comentarios")
    public ResponseEntity<?> getComments(
            @RequestParam(required = false) Long reviewId
    ) {

        return executeSafely(() -> {

            List<Comment> comments;

            // Si no se proporciona un ID de reseña,
            // consultamos todos los comentarios.
            if (reviewId == null) {

                GetAllCommentsQuery query =
                        new GetAllCommentsQuery();

                comments = queryService.handle(query);

            } else {

                // Si recibimos un ID, consultamos solamente
                // los comentarios de esa reseña.
                GetCommentsByReviewIdQuery query =
                        new GetCommentsByReviewIdQuery(reviewId);

                comments = queryService.handle(query);
            }

            // Transformamos las entidades de dominio
            // en recursos REST para Angular.
            List<CommentResource> resources = comments.stream()
                    .map(CommentsController::toResource)
                    .toList();

            // Devolvemos HTTP 200 con la lista.
            return ResponseEntity.ok(resources);
        });
    }

    /**
     * Obtiene un comentario mediante su ID.
     *
     * GET /api/v1/comments/25
     *
     * @param commentId Identificador del comentario.
     * @return Comentario encontrado o HTTP 404.
     */
    @GetMapping("/{commentId}")
    @Operation(summary = "Consultar comentario por ID")
    public ResponseEntity<?> getCommentById(
            @PathVariable Long commentId
    ) {

        return executeSafely(() -> {

            // Construimos la consulta por identificador.
            GetCommentByIdQuery query =
                    new GetCommentByIdQuery(commentId);

            // Buscamos el comentario en el repositorio.
            Optional<Comment> comment =
                    queryService.handle(query);

            // Si no existe, devolvemos HTTP 404.
            if (comment.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Transformamos el comentario en recurso REST.
            CommentResource resource =
                    toResource(comment.get());

            // Devolvemos HTTP 200 con el comentario.
            return ResponseEntity.ok(resource);
        });
    }

    /**
     * Registra un comentario nuevo.
     *
     * POST /api/v1/comments
     *
     * @param resource Datos recibidos desde Angular.
     * @return Comentario creado o error de aplicación.
     */
    @PostMapping
    @Operation(summary = "Crear un comentario")
    public ResponseEntity<?> createComment(
            @RequestBody CreateCommentResource resource
    ) {

        return executeSafely(() -> {

            // Construimos el comando utilizando
            // los datos del recurso REST.
            CreateCommentCommand command =
                    new CreateCommentCommand(
                            resource.reviewId(),
                            resource.content()
                    );

            // Ejecutamos la operación de creación.
            Result<Comment, ApplicationError> result =
                    commandService.handle(command);

            // Convertimos el resultado del servicio
            // en una respuesta HTTP.
            //
            // Si se crea correctamente, devolvemos 201.
            // Si falla, se transforma el ApplicationError.
            return ResponseEntityAssembler
                    .toResponseEntityFromResult(
                            result,
                            CommentsController::toResource,
                            HttpStatus.CREATED
                    );
        });
    }

    /**
     * Actualiza el contenido de un comentario.
     *
     * PUT /api/v1/comments/25
     *
     * @param commentId Identificador del comentario.
     * @param resource Datos actualizados.
     * @return Comentario actualizado o error.
     */
    @PutMapping("/{commentId}")
    @Operation(summary = "Actualizar un comentario")
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @RequestBody CreateCommentResource resource
    ) {

        return executeSafely(() -> {

            // Construimos el comando de actualización.
            UpdateCommentCommand command =
                    new UpdateCommentCommand(
                            commentId,
                            resource.reviewId(),
                            resource.content()
                    );

            // Solicitamos al servicio actualizar
            // el comentario existente.
            Result<Comment, ApplicationError> result =
                    commandService.handle(command);

            // Transformamos el resultado en HTTP.
            // Si es exitoso, devolvemos 200.
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
     *
     * DELETE /api/v1/comments/25
     *
     * @param commentId Identificador del comentario.
     * @return HTTP 204 o el error correspondiente.
     */
    @DeleteMapping("/{commentId}")
    @Operation(summary = "Eliminar un comentario")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId
    ) {

        return executeSafely(() -> {

            // Construimos el comando de eliminación.
            DeleteCommentCommand command =
                    new DeleteCommentCommand(commentId);

            // Ejecutamos la operación de eliminación.
            Result<Boolean, ApplicationError> result =
                    commandService.handle(command);

            // Cuando la operación es exitosa,
            // devolvemos HTTP 204 sin contenido.
            if (result.isSuccess()) {
                return ResponseEntity.noContent().build();
            }

            // Si la operación falló, recuperamos
            // el error que devolvió el servicio.
            if (result instanceof Result.Failure<Boolean, ApplicationError> failure) {

                return ProblemDetailFromApplicationErrorAssembler
                        .toResponseFromApplicationError(
                                failure.error()
                        );
            }

            // Esta situación no debería ocurrir
            // con las implementaciones de Result.
            throw new IllegalStateException(
                    "Estado de resultado no reconocido."
            );
        });
    }

    /**
     * Convierte una entidad del dominio Comment
     * en un recurso de respuesta REST.
     *
     * @param comment Entidad de dominio.
     * @return Recurso REST del comentario.
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
     * Ejecuta una operación REST y captura
     * errores de validación de los Commands
     * y Queries utilizados.
     *
     * @param operation Operación solicitada.
     * @return Respuesta HTTP correspondiente.
     */
    private ResponseEntity<?> executeSafely(
            Supplier<ResponseEntity<?>> operation
    ) {

        try {

            // Ejecutamos la operación solicitada.
            return operation.get();

        } catch (IllegalArgumentException exception) {

            // Creamos un error HTTP 400 para
            // datos inválidos enviados al backend.
            ProblemDetail problem =
                    ProblemDetail.forStatusAndDetail(
                            HttpStatus.BAD_REQUEST,
                            exception.getMessage()
                    );

            return ResponseEntity.badRequest().body(problem);
        }
    }
}
