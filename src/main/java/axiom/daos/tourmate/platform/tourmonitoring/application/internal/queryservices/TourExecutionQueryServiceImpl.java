package axiom.daos.tourmate.platform.tourmonitoring.application.internal.queryservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.tourmonitoring.application.internal.security.TourExecutionAccessPolicy;
import axiom.daos.tourmate.platform.tourmonitoring.application.queryservices.TourExecutionQueryService;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ExecutionIncidentStatus;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ParticipantAttendanceStatus;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.RiskLevel;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourStatus;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.entities.ActiveTourPersistenceEntity;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.entities.TourExecutionPersistenceEntity;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.repositories.ActiveTourPersistenceRepository;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.repositories.TourExecutionPersistenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Read-only query service implementation projecting directly from persistence entities into DTO views.
 */
@Service
@Transactional(readOnly = true)
public class TourExecutionQueryServiceImpl implements TourExecutionQueryService {

    private static final List<TourStatus> ACTIVE_STATUSES = List.of(
            TourStatus.SCHEDULED,
            TourStatus.IN_PROGRESS,
            TourStatus.PAUSED
    );

    private final TourExecutionPersistenceRepository persistenceRepository;
    private final TourExecutionAccessPolicy accessPolicy;
    private final ActiveTourPersistenceRepository activeTourPersistenceRepository;

    public TourExecutionQueryServiceImpl(
            TourExecutionPersistenceRepository persistenceRepository,
            TourExecutionAccessPolicy accessPolicy
    ) {
        this(persistenceRepository, accessPolicy, null);
    }

    @Autowired
    public TourExecutionQueryServiceImpl(
            TourExecutionPersistenceRepository persistenceRepository,
            TourExecutionAccessPolicy accessPolicy,
            ActiveTourPersistenceRepository activeTourPersistenceRepository
    ) {
        this.persistenceRepository = persistenceRepository;
        this.accessPolicy = accessPolicy;
        this.activeTourPersistenceRepository = activeTourPersistenceRepository;
    }

    @Override
    public Result<Page<ActiveTourExecutionItemView>, ApplicationError> findActiveExecutions(
            TourStatus status,
            RiskLevel riskLevel,
            Long guideId,
            Instant fromDate,
            Instant toDate,
            Pageable pageable,
            Set<String> actorRoles
    ) {
        var authError = accessPolicy.requireAdmin(actorRoles);
        if (authError.isPresent()) {
            return Result.failure(authError.get());
        }
        Page<ActiveTourExecutionItemView> page = persistenceRepository.findFilteredActiveExecutions(
                        status,
                        ACTIVE_STATUSES,
                        riskLevel,
                        guideId,
                        fromDate,
                        toDate,
                        pageable
                )
                .map(this::toActiveItemView);
        return Result.success(page);
    }

    @Override
    public Result<TourExecutionLiveView, ApplicationError> getLiveState(
            Long tourExecutionId,
            Long actorUserId,
            Set<String> actorRoles
    ) {
        var entityOpt = persistenceRepository.findById(tourExecutionId);
        if (entityOpt.isEmpty()) {
            return Result.failure(ApplicationError.notFound("TourExecution", tourExecutionId.toString()));
        }
        var entity = entityOpt.get();
        if (!canReadExecution(entity, actorUserId, actorRoles)) {
            return Result.failure(ApplicationError.forbidden(
                    "tour-execution",
                    "Not authorized to view tour execution #" + tourExecutionId
            ));
        }
        return Result.success(toLiveView(entity));
    }

    @Override
    public Result<List<TimelineItemView>, ApplicationError> getTimeline(
            Long tourExecutionId,
            Long actorUserId,
            Set<String> actorRoles
    ) {
        var entityOpt = persistenceRepository.findById(tourExecutionId);
        if (entityOpt.isEmpty()) {
            return Result.failure(ApplicationError.notFound("TourExecution", tourExecutionId.toString()));
        }
        var entity = entityOpt.get();
        if (!canReadExecution(entity, actorUserId, actorRoles)) {
            return Result.failure(ApplicationError.forbidden(
                    "tour-execution-timeline",
                    "Not authorized to view timeline of tour execution #" + tourExecutionId
            ));
        }
        List<TimelineItemView> items = entity.getTimeline().stream()
                .sorted(Comparator.comparing(TourExecutionPersistenceEntity.TimelineEntryEmbeddable::getOccurredAt))
                .map(t -> new TimelineItemView(t.getEntryId(), t.getEventType(), t.getDescription(), t.getOccurredAt()))
                .toList();
        return Result.success(items);
    }

    @Override
    public Result<ParticipantsStatusView, ApplicationError> getParticipantsStatus(
            Long tourExecutionId,
            Long actorUserId,
            Set<String> actorRoles
    ) {
        var entityOpt = persistenceRepository.findById(tourExecutionId);
        if (entityOpt.isEmpty()) {
            return Result.failure(ApplicationError.notFound("TourExecution", tourExecutionId.toString()));
        }
        var entity = entityOpt.get();
        if (!canReadExecution(entity, actorUserId, actorRoles)) {
            return Result.failure(ApplicationError.forbidden(
                    "tour-execution-participants",
                    "Not authorized to view participant status of tour execution #" + tourExecutionId
            ));
        }
        List<ParticipantItemView> participants = entity.getParticipants().stream()
                .map(p -> new ParticipantItemView(
                        p.getParticipantId(),
                        p.getTouristId(),
                        p.getBookingId(),
                        p.getStatus(),
                        p.getCheckedInAt()
                ))
                .toList();

        int present = (int) participants.stream().filter(p -> p.status() == ParticipantAttendanceStatus.PRESENT).count();
        int absent = (int) participants.stream().filter(p -> p.status() == ParticipantAttendanceStatus.ABSENT).count();
        int pending = (int) participants.stream().filter(p -> p.status() == ParticipantAttendanceStatus.PENDING).count();

        return Result.success(new ParticipantsStatusView(
                entity.getId(),
                participants.size(),
                present,
                absent,
                pending,
                participants
        ));
    }

    @Override
    public Result<TourExecutionLiveView, ApplicationError> getCurrentTourForGuide(
            Long guideUserId,
            Set<String> actorRoles
    ) {
        if (!accessPolicy.isGuide(actorRoles) && !accessPolicy.isAdmin(actorRoles)) {
            return Result.failure(ApplicationError.forbidden("guide-current-tour", "Guide role is required"));
        }
        if (guideUserId == null || guideUserId < 1) {
            return Result.failure(ApplicationError.forbidden("guide-current-tour", "Authenticated guide ID is required"));
        }
        var entityOpt = persistenceRepository.findFirstByGuideIdAndStatusInOrderByScheduledStartAtDesc(
                guideUserId,
                ACTIVE_STATUSES
        );
        if (entityOpt.isEmpty()) {
            Long mappedTourGuideId = accessPolicy.resolveTourGuideId(guideUserId);
            if (mappedTourGuideId != null) {
                entityOpt = persistenceRepository.findFirstByGuideIdAndStatusInOrderByScheduledStartAtDesc(
                        mappedTourGuideId,
                        ACTIVE_STATUSES
                );
            }
        }
        if (entityOpt.isPresent()) {
            return Result.success(toLiveView(entityOpt.get()));
        }

        if (activeTourPersistenceRepository != null) {
            var activeTourOpt = activeTourPersistenceRepository.findAll().stream()
                    .filter(at -> at.getTourGuideId() != null
                            && accessPolicy.matchesGuideIdentity(at.getTourGuideId().tourGuideId(), guideUserId))
                    .findFirst();
            if (activeTourOpt.isPresent()) {
                return Result.success(toLiveViewFromActiveTour(activeTourOpt.get()));
            }
        }

        return Result.failure(ApplicationError.notFound("CurrentTourExecution", "guideId=" + guideUserId));
    }

    @Override
    public Result<TourExecutionLiveView, ApplicationError> getTourProgressForTourist(
            Long touristUserId,
            Set<String> actorRoles
    ) {
        if (!accessPolicy.isTourist(actorRoles) && !accessPolicy.isAdmin(actorRoles)) {
            return Result.failure(ApplicationError.forbidden("tourist-tour-progress", "Tourist role is required"));
        }
        if (touristUserId == null || touristUserId < 1) {
            return Result.failure(ApplicationError.forbidden("tourist-tour-progress", "Authenticated tourist ID is required"));
        }
        var executions = persistenceRepository.findByParticipantTouristId(touristUserId);
        if (executions.isEmpty()) {
            return Result.failure(ApplicationError.notFound("TouristTourProgress", "touristId=" + touristUserId));
        }
        var activeOrLatest = executions.stream()
                .filter(e -> ACTIVE_STATUSES.contains(e.getStatus()))
                .findFirst()
                .orElse(executions.getFirst());
        return Result.success(toLiveView(activeOrLatest));
    }

    @Override
    public Result<TourExecutionSummaryView, ApplicationError> getSummary(
            Long tourExecutionId,
            Long actorUserId,
            Set<String> actorRoles
    ) {
        var entityOpt = persistenceRepository.findById(tourExecutionId);
        if (entityOpt.isEmpty()) {
            return Result.failure(ApplicationError.notFound("TourExecution", tourExecutionId.toString()));
        }
        var entity = entityOpt.get();
        if (!canReadExecution(entity, actorUserId, actorRoles)) {
            return Result.failure(ApplicationError.forbidden(
                    "tour-execution-summary",
                    "Not authorized to view summary of tour execution #" + tourExecutionId
            ));
        }

        List<CheckpointView> checkpoints = entity.getCheckpoints().stream()
                .sorted(Comparator.comparingInt(TourExecutionPersistenceEntity.CheckpointEmbeddable::getSequenceOrder))
                .map(this::toCheckpointView)
                .toList();

        List<IncidentView> incidents = entity.getIncidents().stream()
                .map(this::toIncidentView)
                .toList();

        int reachedCount = (int) checkpoints.stream().filter(CheckpointView::reached).count();
        long totalDelay = checkpoints.stream().filter(CheckpointView::reached).mapToLong(CheckpointView::delayMinutes).sum();
        int delayedCount = (int) checkpoints.stream().filter(CheckpointView::delayedBeyondTolerance).count();
        int resolvedIncidents = (int) incidents.stream().filter(i -> i.status() == ExecutionIncidentStatus.RESOLVED).count();
        int presentCount = (int) entity.getParticipants().stream()
                .filter(p -> p.getStatus() == ParticipantAttendanceStatus.PRESENT)
                .count();

        long durationMinutes = 0L;
        if (entity.getStartedAt() != null) {
            Instant end = entity.getCompletedAt() != null
                    ? entity.getCompletedAt()
                    : (entity.getAbortedAt() != null ? entity.getAbortedAt() : Instant.now());
            durationMinutes = Math.max(0L, Duration.between(entity.getStartedAt(), end).toMinutes());
        }

        return Result.success(new TourExecutionSummaryView(
                entity.getId(),
                entity.getTourId(),
                entity.getGuideId(),
                entity.getStatus(),
                entity.getRiskLevel(),
                entity.getScheduledStartAt(),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getAbortedAt(),
                entity.getAbortReason(),
                durationMinutes,
                reachedCount,
                checkpoints.size(),
                totalDelay,
                delayedCount,
                incidents.size(),
                resolvedIncidents,
                incidents.size() - resolvedIncidents,
                presentCount,
                entity.getParticipants().size(),
                checkpoints,
                incidents
        ));
    }

    private boolean canReadExecution(
            TourExecutionPersistenceEntity entity,
            Long actorUserId,
            Set<String> actorRoles
    ) {
        if (accessPolicy.isAdmin(actorRoles)) {
            return true;
        }
        if (accessPolicy.isGuide(actorRoles) && accessPolicy.matchesGuideIdentity(entity.getGuideId(), actorUserId)) {
            return true;
        }
        if (accessPolicy.isTourist(actorRoles) && actorUserId != null) {
            return entity.getParticipants().stream()
                    .anyMatch(p -> actorUserId.equals(p.getTouristId()) || actorUserId.equals(p.getParticipantId()));
        }
        return false;
    }

    private ActiveTourExecutionItemView toActiveItemView(TourExecutionPersistenceEntity entity) {
        int reached = (int) entity.getCheckpoints().stream()
                .filter(TourExecutionPersistenceEntity.CheckpointEmbeddable::isReached)
                .count();
        int openIncidents = (int) entity.getIncidents().stream()
                .filter(i -> i.getStatus() == ExecutionIncidentStatus.OPEN)
                .count();
        int activeAlerts = (int) entity.getAlerts().stream()
                .filter(TourExecutionPersistenceEntity.AlertEmbeddable::isActive)
                .count();
        long currentDelay = entity.getCheckpoints().stream()
                .filter(TourExecutionPersistenceEntity.CheckpointEmbeddable::isReached)
                .max(Comparator.comparingInt(TourExecutionPersistenceEntity.CheckpointEmbeddable::getSequenceOrder))
                .map(TourExecutionPersistenceEntity.CheckpointEmbeddable::getDelayMinutes)
                .orElse(0L);

        return new ActiveTourExecutionItemView(
                entity.getId(),
                entity.getTourId(),
                entity.getGuideId(),
                entity.getBookingId(),
                entity.getStatus(),
                entity.getRiskLevel(),
                entity.getScheduledStartAt(),
                entity.getStartedAt(),
                reached,
                entity.getCheckpoints().size(),
                openIncidents,
                activeAlerts,
                currentDelay
        );
    }

    private TourExecutionLiveView toLiveView(TourExecutionPersistenceEntity entity) {
        List<CheckpointView> checkpoints = entity.getCheckpoints().stream()
                .sorted(Comparator.comparingInt(TourExecutionPersistenceEntity.CheckpointEmbeddable::getSequenceOrder))
                .map(this::toCheckpointView)
                .toList();

        CheckpointView nextCheckpoint = checkpoints.stream()
                .filter(cp -> !cp.reached())
                .findFirst()
                .orElse(null);

        long currentDelay = checkpoints.stream()
                .filter(CheckpointView::reached)
                .reduce((_, second) -> second)
                .map(CheckpointView::delayMinutes)
                .orElse(0L);

        List<ParticipantItemView> participants = entity.getParticipants().stream()
                .map(p -> new ParticipantItemView(
                        p.getParticipantId(),
                        p.getTouristId(),
                        p.getBookingId(),
                        p.getStatus(),
                        p.getCheckedInAt()
                ))
                .toList();

        List<IncidentView> incidents = entity.getIncidents().stream()
                .map(this::toIncidentView)
                .toList();

        List<AlertView> activeAlerts = entity.getAlerts().stream()
                .filter(TourExecutionPersistenceEntity.AlertEmbeddable::isActive)
                .map(a -> new AlertView(
                        a.getAlertId(),
                        a.getType(),
                        a.getSeverity(),
                        a.getMessage(),
                        a.getSourceUserId(),
                        a.getSourceRole(),
                        a.getLatitude(),
                        a.getLongitude(),
                        a.isActive(),
                        a.getCreatedAt()
                ))
                .toList();

        return new TourExecutionLiveView(
                entity.getId(),
                entity.getTourId(),
                entity.getGuideId(),
                entity.getBookingId(),
                entity.getStatus(),
                entity.getRiskLevel(),
                entity.getCurrentLatitude(),
                entity.getCurrentLongitude(),
                entity.getScheduledStartAt(),
                entity.getStartedAt(),
                entity.getPausedAt(),
                entity.getPauseReason(),
                currentDelay,
                nextCheckpoint,
                checkpoints,
                participants,
                incidents,
                activeAlerts
        );
    }

    private CheckpointView toCheckpointView(TourExecutionPersistenceEntity.CheckpointEmbeddable cp) {
        return new CheckpointView(
                cp.getCheckpointId(),
                cp.getName(),
                cp.getSequenceOrder(),
                cp.getLatitude(),
                cp.getLongitude(),
                cp.getExpectedArrival(),
                cp.getLatestAllowedArrival(),
                cp.isReached(),
                cp.getReachedAt(),
                cp.getDelayMinutes(),
                cp.isDelayedBeyondTolerance()
        );
    }

    private IncidentView toIncidentView(TourExecutionPersistenceEntity.IncidentEmbeddable inc) {
        return new IncidentView(
                inc.getIncidentId(),
                inc.getType(),
                inc.getSeverity(),
                inc.getDescription(),
                inc.getLatitude(),
                inc.getLongitude(),
                inc.getReportedById(),
                inc.getReportedByRole(),
                inc.getStatus(),
                inc.getResolutionNotes(),
                inc.getReportedAt(),
                inc.getResolvedAt()
        );
    }

    private TourExecutionLiveView toLiveViewFromActiveTour(ActiveTourPersistenceEntity at) {
        Long scheduleId = at.getTourScheduleId() != null ? at.getTourScheduleId().tourScheduleId() : 1L;
        Long guideId = at.getTourGuideId() != null ? at.getTourGuideId().tourGuideId() : 1L;
        TourStatus status = (at.getFinishedAt() != null && !at.getFinishedAt().isBlank())
                ? TourStatus.COMPLETED
                : TourStatus.IN_PROGRESS;
        Instant now = Instant.now();
        return new TourExecutionLiveView(
                at.getId(),
                scheduleId,
                guideId,
                scheduleId,
                status,
                RiskLevel.LOW,
                at.getCurrentLatitude(),
                at.getCurrentLongitude(),
                now,
                now,
                null,
                null,
                0L,
                null,
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }
}
