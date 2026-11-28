package axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.tourmonitoring.application.commandservices.TourExecutionCommandService;
import axiom.daos.tourmate.platform.tourmonitoring.application.queryservices.TourExecutionQueryService;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.CreateTourExecutionCommand;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Alert;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionAlertType;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.GeoLocation;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.RiskLevel;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourStatus;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.resources.TourExecutionResources;
import axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.support.AuthenticatedActorResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TourExecutionsController} HTTP status codes and ProblemDetail error mapping.
 */
@ExtendWith(MockitoExtension.class)
class TourExecutionsControllerTest {

    @Mock
    private TourExecutionCommandService commandService;

    @Mock
    private TourExecutionQueryService queryService;

    @Mock
    private AuthenticatedActorResolver actorResolver;

    @Mock
    private HttpServletRequest request;

    private TourExecutionsController controller;

    @BeforeEach
    void setUp() {
        controller = new TourExecutionsController(commandService, queryService, actorResolver);
        when(actorResolver.resolve(any())).thenReturn(
                new AuthenticatedActorResolver.AuthenticatedActor(2L, Set.of("ROLE_TOUR_GUIDE"))
        );
    }

    @Test
    @DisplayName("POST /api/v1/tour-executions/{id}/start should return 422 ProblemDetail on INSUFFICIENT_CHECK_INS")
    void shouldReturn422WhenStartViolatesMinCheckInsBusinessRule() {
        when(commandService.handle(any(CreateTourExecutionCommand.StartTourExecutionCommand.class)))
                .thenReturn(Result.failure(ApplicationError.domainRule(
                        "INSUFFICIENT_CHECK_INS",
                        "Business rule violated",
                        "At least 1 checked-in participants are required to start"
                )));

        var response = controller.startTourExecution(1L, request);

        assertEquals(422, response.getStatusCode().value());
        var problem = assertInstanceOf(ProblemDetail.class, response.getBody());
        assertEquals("INSUFFICIENT_CHECK_INS", problem.getProperties().get("errorCode"));
    }

    @Test
    @DisplayName("POST /api/v1/tour-executions/{id}/pause should return 409 ProblemDetail on INVALID_STATE_TRANSITION")
    void shouldReturn409WhenPauseIsInvalidStateTransition() {
        when(commandService.handle(any(CreateTourExecutionCommand.PauseTourExecutionCommand.class)))
                .thenReturn(Result.failure(ApplicationError.stateConflict(
                        "INVALID_STATE_TRANSITION",
                        "Tour execution conflict",
                        "Cannot pause tour execution from status SCHEDULED"
                )));

        var response = controller.pauseTourExecution(
                1L,
                new TourExecutionResources.ReasonResource("Weather warning"),
                request
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        var problem = assertInstanceOf(ProblemDetail.class, response.getBody());
        assertEquals("INVALID_STATE_TRANSITION", problem.getProperties().get("errorCode"));
    }

    @Test
    @DisplayName("POST /api/v1/tour-executions/{id}/start should return 403 ProblemDetail on FORBIDDEN")
    void shouldReturn403WhenGuideDoesNotOwnTour() {
        when(commandService.handle(any(CreateTourExecutionCommand.StartTourExecutionCommand.class)))
                .thenReturn(Result.failure(ApplicationError.forbidden(
                        "tour-execution",
                        "Only assigned guide #2 can operate tour execution"
                )));

        var response = controller.startTourExecution(1L, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        var problem = assertInstanceOf(ProblemDetail.class, response.getBody());
        assertEquals("FORBIDDEN", problem.getProperties().get("errorCode"));
    }

    @Test
    @DisplayName("POST /api/v1/tour-executions/{id}/sos should return 201 Created with ExecutionAlertResource DTO")
    void shouldReturn201CreatedWithDtoWhenTriggeringSos() {
        var alert = new Alert(
                1L,
                ExecutionAlertType.SOS,
                RiskLevel.CRITICAL,
                "SOS emergency triggered by GUIDE #2",
                2L,
                "GUIDE",
                new GeoLocation(-13.5167, -71.9788),
                true,
                Instant.parse("2026-12-01T08:30:00Z")
        );
        when(commandService.handle(any(CreateTourExecutionCommand.TriggerSosCommand.class)))
                .thenReturn(Result.success(alert));

        var response = controller.triggerSos(
                1L,
                new TourExecutionResources.TriggerSosResource("Medical emergency", -13.5167, -71.9788),
                request
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        var resource = assertInstanceOf(TourExecutionResources.ExecutionAlertResource.class, response.getBody());
        assertEquals(ExecutionAlertType.SOS, resource.type());
        assertEquals(RiskLevel.CRITICAL, resource.riskLevel());
    }

    @Test
    @DisplayName("GET /api/v1/tour-executions/{id}/live should return 200 OK with TourExecutionLiveView")
    void shouldReturn200WithLiveStateProjection() {
        var liveView = new TourExecutionQueryService.TourExecutionLiveView(
                1L,
                10L,
                2L,
                100L,
                TourStatus.IN_PROGRESS,
                RiskLevel.MODERATE,
                -13.5167,
                -71.9788,
                Instant.parse("2026-12-01T08:00:00Z"),
                Instant.parse("2026-12-01T08:02:00Z"),
                null,
                null,
                0L,
                null,
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
        when(queryService.getLiveState(1L, 2L, Set.of("ROLE_TOUR_GUIDE")))
                .thenReturn(Result.success(liveView));

        var response = controller.getLiveState(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        var body = assertInstanceOf(TourExecutionQueryService.TourExecutionLiveView.class, response.getBody());
        assertEquals(TourStatus.IN_PROGRESS, body.status());
        assertEquals(RiskLevel.MODERATE, body.riskLevel());
    }
}
