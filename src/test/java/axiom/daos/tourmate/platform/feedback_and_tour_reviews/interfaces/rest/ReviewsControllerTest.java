
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.commandservices.ReviewCommandService;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.queryservices.ReviewQueryService;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetAllReviewsQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetReviewByIdQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetReviewsByTourIdQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources.CreateReviewResource;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources.UpdateReviewResource;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources.ReviewResource;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewsControllerTest {

    @Mock
    private ReviewCommandService commandService;

    @Mock
    private ReviewQueryService queryService;

    private ReviewsController controller;

    @BeforeEach
    void setUp() {
        controller = new ReviewsController(
                commandService,
                queryService
        );
    }

    private Review existingReview() {
        return Review.reconstitute(
                9L, 1L, 3L, 5, Instant.now()
        );
    }

    @Test
    void shouldListAllReviews() {
        when(queryService.handle(any(GetAllReviewsQuery.class)))
                .thenReturn(List.of(existingReview()));

        ResponseEntity<?> response = controller.getReviews(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<?> body = assertInstanceOf(
                List.class, response.getBody()
        );

        assertEquals(1, body.size());

        ReviewResource resource = assertInstanceOf(
                ReviewResource.class, body.get(0)
        );

        assertEquals(Long.valueOf(9), resource.id());
        assertEquals(Integer.valueOf(5), resource.rating());
    }

    @Test
    void shouldFilterReviewsByTour() {
        when(queryService.handle(any(GetReviewsByTourIdQuery.class)))
                .thenReturn(List.of(existingReview()));

        ResponseEntity<?> response = controller.getReviews(3L);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<?> body = assertInstanceOf(
                List.class, response.getBody()
        );

        assertEquals(1, body.size());

        verify(queryService)
                .handle(any(GetReviewsByTourIdQuery.class));
    }

    @Test
    void shouldFindReviewById() {
        when(queryService.handle(any(GetReviewByIdQuery.class)))
                .thenReturn(Optional.of(existingReview()));

        ResponseEntity<?> response = controller.getReviewById(9L);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ReviewResource resource = assertInstanceOf(
                ReviewResource.class, response.getBody()
        );

        assertEquals(Long.valueOf(9), resource.id());
        assertEquals(Long.valueOf(1), resource.userId());
        assertEquals(Long.valueOf(3), resource.tourId());
    }

    @Test
    void shouldReturnNotFoundForMissingReview() {
        when(queryService.handle(any(GetReviewByIdQuery.class)))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getReviewById(99L);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );
    }

    @Test
    void shouldCreateReview() {
        when(commandService.handle(any(CreateReviewCommand.class)))
                .thenReturn(Result.success(existingReview()));

        ResponseEntity<?> response = controller.createReview(
                new CreateReviewResource(1L, 3L, 5)
        );

        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );

        ReviewResource resource = assertInstanceOf(
                ReviewResource.class, response.getBody()
        );

        assertEquals(Long.valueOf(9), resource.id());
    }

    @Test
    void shouldRejectInvalidRating() {
        ResponseEntity<?> response = controller.createReview(
                new CreateReviewResource(1L, 3L, 6)
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertInstanceOf(
                ProblemDetail.class,
                response.getBody()
        );

        verifyNoInteractions(commandService);
    }

    @Test
    void shouldUpdateReviewRating() {
        Review review = existingReview();
        review.updateRating(0);

        when(commandService.handle(any(UpdateReviewCommand.class)))
                .thenReturn(Result.success(review));

        ResponseEntity<?> response = controller.updateReview(
                9L,
                new UpdateReviewResource(1L, 3L, 0)
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ReviewResource resource = assertInstanceOf(
                ReviewResource.class, response.getBody()
        );

        assertEquals(Integer.valueOf(0), resource.rating());
    }

    @Test
    void shouldReturnConflictForDuplicateReview() {
        when(commandService.handle(any(CreateReviewCommand.class)))
                .thenReturn(
                        Result.failure(
                                ApplicationError.conflict(
                                        "Review",
                                        "El usuario ya calificó este tour."
                                )
                        )
                );

        ResponseEntity<?> response = controller.createReview(
                new CreateReviewResource(1L, 3L, 5)
        );

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        assertInstanceOf(
                ProblemDetail.class,
                response.getBody()
        );
    }

    @Test
    void shouldDeleteReview() {
        when(commandService.handle(any(DeleteReviewCommand.class)))
                .thenReturn(Result.success(true));

        ResponseEntity<?> response = controller.deleteReview(9L);

        assertEquals(
                HttpStatus.NO_CONTENT,
                response.getStatusCode()
        );

        verify(commandService)
                .handle(any(DeleteReviewCommand.class));
    }
}
