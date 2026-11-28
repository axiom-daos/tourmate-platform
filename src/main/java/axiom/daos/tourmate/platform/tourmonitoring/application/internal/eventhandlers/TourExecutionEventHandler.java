package axiom.daos.tourmate.platform.tourmonitoring.application.internal.eventhandlers;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.CheckpointReached;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.IncidentReported;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.IncidentResolved;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.RiskLevelChanged;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.SosTriggered;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.TourAborted;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.TourCompleted;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.TourPaused;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.TourResumed;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.events.TourStarted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Consumes {@code TourExecution} domain events via {@link TransactionalEventListener}.
 */
@Service
public class TourExecutionEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TourExecutionEventHandler.class);

    private final List<Object> publishedEventsLog = new CopyOnWriteArrayList<>();

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(TourStarted event) {
        publishedEventsLog.add(event);
        LOGGER.info("TourExecution #{} started for tour #{} by guide #{}",
                event.tourExecutionId(), event.tourId(), event.guideId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(CheckpointReached event) {
        publishedEventsLog.add(event);
        LOGGER.info("TourExecution #{} reached checkpoint #{} ({}) with delay={} min",
                event.tourExecutionId(), event.checkpointId(), event.checkpointName(), event.delayMinutes());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(IncidentReported event) {
        publishedEventsLog.add(event);
        LOGGER.info("TourExecution #{} reported incident #{} [{} - {}]",
                event.tourExecutionId(), event.incidentId(), event.type(), event.severity());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(IncidentResolved event) {
        publishedEventsLog.add(event);
        LOGGER.info("TourExecution #{} resolved incident #{}", event.tourExecutionId(), event.incidentId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(RiskLevelChanged event) {
        publishedEventsLog.add(event);
        LOGGER.warn("TourExecution #{} risk escalated: {} -> {} ({})",
                event.tourExecutionId(), event.previousRiskLevel(), event.newRiskLevel(), event.reason());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(SosTriggered event) {
        publishedEventsLog.add(event);
        LOGGER.error("CRITICAL SOS triggered on TourExecution #{} by {} #{}: {}",
                event.tourExecutionId(), event.triggeredByRole(), event.triggeredByUserId(), event.reason());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(TourPaused event) {
        publishedEventsLog.add(event);
        LOGGER.info("TourExecution #{} paused: {}", event.tourExecutionId(), event.reason());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(TourResumed event) {
        publishedEventsLog.add(event);
        LOGGER.info("TourExecution #{} resumed", event.tourExecutionId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(TourCompleted event) {
        publishedEventsLog.add(event);
        LOGGER.info("TourExecution #{} completed in {} minutes",
                event.tourExecutionId(), event.totalDurationMinutes());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(TourAborted event) {
        publishedEventsLog.add(event);
        LOGGER.warn("TourExecution #{} aborted: {}", event.tourExecutionId(), event.reason());
    }

    public List<Object> getPublishedEventsLog() {
        return List.copyOf(publishedEventsLog);
    }
}
