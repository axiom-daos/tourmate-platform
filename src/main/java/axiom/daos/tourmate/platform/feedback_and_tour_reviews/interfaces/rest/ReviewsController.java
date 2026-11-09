
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.commandservices.ReviewCommandService;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.queryservices.ReviewQueryService;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteReviewCommand;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetAllReviewsQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetReviewByIdQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetReviewsByTourIdQuery;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources.CreateReviewResource;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources.UpdateReviewResource;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.transform.CreateReviewCommandFromResourceAssembler;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.transform.ReviewResourceFromEntityAssembler;

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
 * Controlador REST para gestionar reseñas turísticas.
 *
 * Expone las operaciones CRUD mediante HTTP.
 * Delega las reglas del negocio a los servicios
 * de aplicación y no accede directamente a JPA.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@RestController
@RequestMapping(
        value = "/api/v1/reviews",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(
        name = "Reviews",
        description = "Gestión de reseñas turísticas"
)
public class ReviewsController {

    // Servicio encargado de modificar las reseñas.
    private final ReviewCommandService commandService;

    // Servicio encargado de consultar las reseñas.
    private final ReviewQueryService queryService;

    /**
     * Constructor con inyección de dependencias.
     */
    public ReviewsController(
            ReviewCommandService commandService,
            ReviewQueryService queryService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    /**
     * Obtiene todas las reseñas o filtra por tour.
     *
     * Ejemplos:
     * GET /api/v1/reviews
     * GET /api/v1/reviews?tourId=3
     */
    @GetMapping
    @Operation(summary = "Consultar reseñas")
    public ResponseEntity<?> getReviews(
            @RequestParam(required = false) Long tourId
    ) {

        return executeSafely(() -> {

            // Elegimos la consulta según el filtro recibido.
            var reviews = tourId == null
                    ? queryService.handle(new GetAllReviewsQuery())
                    : queryService.handle(
                    new GetReviewsByTourIdQuery(tourId)
            );

            // Convertimos las reseñas en recursos REST.
            var resources = reviews.stream()
                    .map(
                            ReviewResourceFromEntityAssembler
                                    ::toResourceFromEntity
                    )
                    .toList();

            return ResponseEntity.ok(resources);
        });
    }

    /**
     * Obtiene una reseña utilizando su ID.
     *
     * GET /api/v1/reviews/15
     */
    @GetMapping("/{reviewId}")
    @Operation(summary = "Consultar una reseña por ID")
    public ResponseEntity<?> getReviewById(
            @PathVariable Long reviewId
    ) {

        return executeSafely(() -> {

            // Construimos el Query y buscamos la reseña.
            var review = queryService.handle(
                    new GetReviewByIdQuery(reviewId)
            );

            // Devolvemos 404 cuando no existe.
            if (review.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(
                    ReviewResourceFromEntityAssembler
                            .toResourceFromEntity(review.get())
            );
        });
    }

    /**
     * Crea una nueva reseña.
     *
     * POST /api/v1/reviews
     */
    @PostMapping
    @Operation(summary = "Crear una reseña")
    public ResponseEntity<?> createReview(
            @RequestBody CreateReviewResource resource
    ) {

        return executeSafely(() -> {

            // Convertimos el recurso recibido en un Command.
            CreateReviewCommand command =
                    CreateReviewCommandFromResourceAssembler
                            .toCommandFromResource(resource);

            // Solicitamos la creación al servicio.
            var result = commandService.handle(command);

            // Convertimos el resultado en una respuesta HTTP.
            // Si se crea correctamente, devolvemos 201.
            return ResponseEntityAssembler
                    .toResponseEntityFromResult(
                            result,
                            ReviewResourceFromEntityAssembler
                                    ::toResourceFromEntity,
                            HttpStatus.CREATED
                    );
        });
    }

    /**
     * Actualiza una reseña existente.
     *
     * PUT /api/v1/reviews/15
     */
    @PutMapping("/{reviewId}")
    @Operation(summary = "Actualizar una reseña")
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @RequestBody UpdateReviewResource resource
    ) {

        return executeSafely(() -> {

            // El ID proviene de la URL.
            UpdateReviewCommand command =
                    new UpdateReviewCommand(
                            reviewId,
                            resource.userId(),
                            resource.tourId(),
                            resource.rating()
                    );

            // Ejecutamos la actualización.
            var result = commandService.handle(command);

            // Devolvemos 200 con la reseña actualizada
            // o el error correspondiente.
            return ResponseEntityAssembler
                    .toResponseEntityFromResult(
                            result,
                            ReviewResourceFromEntityAssembler
                                    ::toResourceFromEntity,
                            HttpStatus.OK
                    );
        });
    }

    /**
     * Elimina una reseña y sus comentarios asociados.
     *
     * DELETE /api/v1/reviews/15
     */
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Eliminar una reseña")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long reviewId
    ) {

        return executeSafely(() -> {

            // Construimos el comando de eliminación.
            DeleteReviewCommand command =
                    new DeleteReviewCommand(reviewId);

            // Solicitamos la eliminación al servicio.
            var result = commandService.handle(command);

            // Si se elimina correctamente, devolvemos
            // HTTP 204 sin contenido.
            return ResponseEntityAssembler
                    .toResponseEntityFromResult(
                            result,
                            deleted -> null,
                            HttpStatus.NO_CONTENT
                    );
        });
    }

    /**
     * Captura errores de validación de los Commands
     * y Queries para responder con HTTP 400.
     *
     * Los errores de negocio devueltos por los
     * servicios se procesan por separado mediante
     * ResponseEntityAssembler.
     */
    private ResponseEntity<?> executeSafely(
            Supplier<ResponseEntity<?>> operation
    ) {

        try {
            return operation.get();

        } catch (IllegalArgumentException exception) {

            // Construimos una respuesta de error estándar.
            ProblemDetail problem =
                    ProblemDetail.forStatusAndDetail(
                            HttpStatus.BAD_REQUEST,
                            exception.getMessage()
                    );

            return ResponseEntity.badRequest().body(problem);
        }
    }
}
