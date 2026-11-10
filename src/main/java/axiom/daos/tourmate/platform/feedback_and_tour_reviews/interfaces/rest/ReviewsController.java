
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.commandservices.ReviewCommandService;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.queryservices.ReviewQueryService;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateReviewCommand;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetAllReviewsQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetReviewByIdQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetReviewsByTourIdQuery;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources.CreateReviewResource;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources.UpdateReviewResource;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.transform.CreateReviewCommandFromResourceAssembler;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.transform.ReviewResourceFromEntityAssembler;

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
 * Controlador REST para gestionar reseñas turísticas.
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

    private final ReviewCommandService commandService;
    private final ReviewQueryService queryService;

    public ReviewsController(
            ReviewCommandService commandService,
            ReviewQueryService queryService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    /**
     * Obtiene todas las reseñas o filtra por tour.
     */
    @GetMapping
    @Operation(summary = "Consultar reseñas")
    public ResponseEntity<?> getReviews(
            @RequestParam(required = false) Long tourId
    ) {

        return executeSafely(() -> {

            var reviews = tourId == null
                    ? queryService.handle(new GetAllReviewsQuery())
                    : queryService.handle(
                    new GetReviewsByTourIdQuery(tourId)
            );

            var resources = reviews.stream()
                    .map(ReviewResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();

            return ResponseEntity.ok(resources);
        });
    }

    /**
     * Obtiene una reseña por su identificador.
     */
    @GetMapping("/{reviewId}")
    @Operation(summary = "Consultar reseña por ID")
    public ResponseEntity<?> getReviewById(
            @PathVariable Long reviewId
    ) {

        return executeSafely(() -> {

            var review = queryService.handle(
                    new GetReviewByIdQuery(reviewId)
            );

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
     * Registra una nueva reseña.
     */
    @PostMapping
    @Operation(summary = "Crear una reseña")
    public ResponseEntity<?> createReview(
            @RequestBody CreateReviewResource resource
    ) {

        return executeSafely(() -> {

            var command = CreateReviewCommandFromResourceAssembler
                    .toCommandFromResource(resource);

            var result = commandService.handle(command);

            return ResponseEntityAssembler
                    .toResponseEntityFromResult(
                            result,
                            ReviewResourceFromEntityAssembler::toResourceFromEntity,
                            HttpStatus.CREATED
                    );
        });
    }

    /**
     * Actualiza solamente la calificación.
     */
    @PutMapping("/{reviewId}")
    @Operation(summary = "Actualizar una reseña")
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @RequestBody UpdateReviewResource resource
    ) {

        return executeSafely(() -> {

            UpdateReviewCommand command = new UpdateReviewCommand(
                    reviewId,
                    resource.userId(),
                    resource.tourId(),
                    resource.rating()
            );

            var result = commandService.handle(command);

            return ResponseEntityAssembler
                    .toResponseEntityFromResult(
                            result,
                            ReviewResourceFromEntityAssembler::toResourceFromEntity,
                            HttpStatus.OK
                    );
        });
    }

    /**
     * Elimina una reseña y sus comentarios asociados.
     */
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Eliminar una reseña")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long reviewId
    ) {

        return executeSafely(() -> {

            var result = commandService.handle(
                    new DeleteReviewCommand(reviewId)
            );

            if (result instanceof Result.Failure<Boolean, ApplicationError> failure) {
                return ProblemDetailFromApplicationErrorAssembler
                        .toResponseFromApplicationError(failure.error());
            }

            return ResponseEntity.noContent().build();
        });
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
