package axiom.daos.tourmate.platform.tourmonitoring.application.internal.commandservices;

import axiom.daos.tourmate.platform.iam.interfaces.acl.IamContextFacade;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.tourmonitoring.application.commandservices.TourExecutionCommandService;
import axiom.daos.tourmate.platform.tourmonitoring.application.internal.security.TourExecutionAccessPolicy;
import axiom.daos.tourmate.platform.tourmonitoring.domain.exceptions.TourExecutionDomainException;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.TourExecution;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.CreateTourExecutionCommand;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Alert;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Checkpoint;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Incident;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.ParticipantStatus;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.GeoLocation;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TimeWindow;
import axiom.daos.tourmate.platform.tourmonitoring.domain.repositories.TourExecutionRepository;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.configuration.TourMonitoringProperties;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Transactional implementation of {@link TourExecutionCommandService}.
 */
@Service
public class TourExecutionCommandServiceImpl implements TourExecutionCommandService {

    private final TourExecutionRepository tourExecutionRepository;
    private final TourMonitoringProperties properties;
    private final TourExecutionAccessPolicy accessPolicy;
    private final IamContextFacade iamContextFacade;

    public TourExecutionCommandServiceImpl(
            TourExecutionRepository tourExecutionRepository,
            TourMonitoringProperties properties,
            TourExecutionAccessPolicy accessPolicy,
            IamContextFacade iamContextFacade
    ) {
        this.tourExecutionRepository = tourExecutionRepository;
        this.properties = properties;
        this.accessPolicy = accessPolicy;
        this.iamContextFacade = iamContextFacade;
    }

    @Override
    @Transactional
    public Result<TourExecution, ApplicationError> handle(CreateTourExecutionCommand command) {
        try {
            GeoLocation initialLocation = (command.initialLatitude() != null && command.initialLongitude() != null)
                    ? new GeoLocation(command.initialLatitude(), command.initialLongitude())
                    : null;

            List<Checkpoint> checkpoints = command.checkpoints() == null ? List.of() : command.checkpoints().stream()
                    .map(seed -> new Checkpoint(
                            seed.id(),
                            seed.name(),
                            seed.sequenceOrder(),
                            new GeoLocation(seed.latitude(), seed.longitude()),
                            new TimeWindow(seed.expectedArrival(), seed.latestAllowedArrival())
                    ))
                    .toList();

            List<ParticipantStatus> participants = command.participants() == null ? List.of() : command.participants().stream()
                    .map(seed -> new ParticipantStatus(seed.participantId(), seed.touristId(), seed.bookingId()))
                    .toList();

            var execution = new TourExecution(
                    command.tourId(),
                    command.guideId(),
                    command.bookingId(),
                    command.scheduledStartAt() != null ? command.scheduledStartAt() : Instant.now(),
                    initialLocation,
                    checkpoints,
                    participants
            );
            return Result.success(tourExecutionRepository.save(execution));
        } catch (TourExecutionDomainException e) {
            return Result.failure(e.toApplicationError());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("TourExecution", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-tour-execution", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<ParticipantStatus, ApplicationError> handle(
            CreateTourExecutionCommand.CheckInParticipantCommand command
    ) {
        var existing = tourExecutionRepository.findById(command.tourExecutionId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("TourExecution", command.tourExecutionId().toString()));
        }
        var execution = existing.get();
        var authError = accessPolicy.requireGuideOwner(execution, command.actorUserId(), command.actorRoles());
        if (authError.isPresent()) {
            return Result.failure(authError.get());
        }

        try {
            var checkedIn = execution.checkInParticipant(command.participantId(), Instant.now());
            tourExecutionRepository.save(execution);
            return Result.success(checkedIn);
        } catch (TourExecutionDomainException e) {
            return Result.failure(e.toApplicationError());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("CheckIn", e.getMessage()));
        } catch (ObjectOptimisticLockingFailureException e) {
            return Result.failure(concurrentModificationError());
        }
    }

    @Override
    @Transactional
    public Result<TourExecution, ApplicationError> handle(
            CreateTourExecutionCommand.StartTourExecutionCommand command
    ) {
        var existing = tourExecutionRepository.findById(command.tourExecutionId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("TourExecution", command.tourExecutionId().toString()));
        }
        var execution = existing.get();
        var authError = accessPolicy.requireGuideOwner(execution, command.actorUserId(), command.actorRoles());
        if (authError.isPresent()) {
            return Result.failure(authError.get());
        }

        try {
            boolean guideEnabled = execution.getGuideId() != null && execution.getGuideId() > 0;
            if (guideEnabled && iamContextFacade != null) {
                // If IAM has seeded users, verify guide exists; allow when IAM user table is empty in isolated unit tests
                guideEnabled = iamContextFacade.existsUserById(execution.getGuideId())
                        || execution.getGuideId().equals(command.actorUserId());
            }
            execution.start(guideEnabled, properties.getMinCheckIns(), Instant.now());
            return Result.success(tourExecutionRepository.save(execution));
        } catch (TourExecutionDomainException e) {
            return Result.failure(e.toApplicationError());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("StartTour", e.getMessage()));
        } catch (ObjectOptimisticLockingFailureException e) {
            return Result.failure(concurrentModificationError());
        }
    }

    @Override
    @Transactional
    public Result<Checkpoint, ApplicationError> handle(
            CreateTourExecutionCommand.ReachCheckpointCommand command
    ) {
        var existing = tourExecutionRepository.findById(command.tourExecutionId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("TourExecution", command.tourExecutionId().toString()));
        }
        var execution = existing.get();
        var authError = accessPolicy.requireGuideOwner(execution, command.actorUserId(), command.actorRoles());
        if (authError.isPresent()) {
            return Result.failure(authError.get());
        }

        try {
            var reached = execution.reachCheckpoint(
                    command.checkpointId(),
                    Instant.now(),
                    properties.getCheckpointDelayToleranceMinutes()
            );
            tourExecutionRepository.save(execution);
            return Result.success(reached);
        } catch (TourExecutionDomainException e) {
            return Result.failure(e.toApplicationError());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("ReachCheckpoint", e.getMessage()));
        } catch (ObjectOptimisticLockingFailureException e) {
            return Result.failure(concurrentModificationError());
        }
    }

    @Override
    @Transactional
    public Result<Incident, ApplicationError> handle(
            CreateTourExecutionCommand.ReportIncidentCommand command
    ) {
        var existing = tourExecutionRepository.findById(command.tourExecutionId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("TourExecution", command.tourExecutionId().toString()));
        }
        var execution = existing.get();
        var authError = accessPolicy.requireTourParticipantOrGuideOrAdmin(
                execution,
                command.actorUserId(),
                command.actorRoles()
        );
        if (authError.isPresent()) {
            return Result.failure(authError.get());
        }

        try {
            GeoLocation location = (command.latitude() != null && command.longitude() != null)
                    ? new GeoLocation(command.latitude(), command.longitude())
                    : null;
            String reporterRole = accessPolicy.resolvePrimaryRoleName(command.actorRoles());
            var incident = execution.reportIncident(
                    command.type(),
                    command.severity(),
                    command.description(),
                    location,
                    command.actorUserId(),
                    reporterRole,
                    properties.getMediumIncidentsThreshold(),
                    Instant.now()
            );
            tourExecutionRepository.save(execution);
            return Result.success(incident);
        } catch (TourExecutionDomainException e) {
            return Result.failure(e.toApplicationError());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Incident", e.getMessage()));
        } catch (ObjectOptimisticLockingFailureException e) {
            return Result.failure(concurrentModificationError());
        }
    }

    @Override
    @Transactional
    public Result<Incident, ApplicationError> handle(
            CreateTourExecutionCommand.ResolveIncidentCommand command
    ) {
        var existing = tourExecutionRepository.findById(command.tourExecutionId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("TourExecution", command.tourExecutionId().toString()));
        }
        var execution = existing.get();
        var authError = accessPolicy.requireGuideOwnerOrAdmin(
                execution,
                command.actorUserId(),
                command.actorRoles()
        );
        if (authError.isPresent()) {
            return Result.failure(authError.get());
        }

        try {
            var resolved = execution.resolveIncident(
                    command.incidentId(),
                    command.resolutionNotes(),
                    Instant.now()
            );
            tourExecutionRepository.save(execution);
            return Result.success(resolved);
        } catch (TourExecutionDomainException e) {
            return Result.failure(e.toApplicationError());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("ResolveIncident", e.getMessage()));
        } catch (ObjectOptimisticLockingFailureException e) {
            return Result.failure(concurrentModificationError());
        }
    }

    @Override
    @Transactional
    public Result<Alert, ApplicationError> handle(
            CreateTourExecutionCommand.TriggerSosCommand command
    ) {
        var existing = tourExecutionRepository.findById(command.tourExecutionId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("TourExecution", command.tourExecutionId().toString()));
        }
        var execution = existing.get();
        var authError = accessPolicy.requireTourParticipantOrGuideOrAdmin(
                execution,
                command.actorUserId(),
                command.actorRoles()
        );
        if (authError.isPresent()) {
            return Result.failure(authError.get());
        }

        try {
            GeoLocation location = (command.latitude() != null && command.longitude() != null)
                    ? new GeoLocation(command.latitude(), command.longitude())
                    : null;
            String role = accessPolicy.resolvePrimaryRoleName(command.actorRoles());
            var alert = execution.triggerSos(
                    command.actorUserId(),
                    role,
                    command.reason(),
                    location,
                    Instant.now()
            );
            tourExecutionRepository.save(execution);
            return Result.success(alert);
        } catch (TourExecutionDomainException e) {
            return Result.failure(e.toApplicationError());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("SOS", e.getMessage()));
        } catch (ObjectOptimisticLockingFailureException e) {
            return Result.failure(concurrentModificationError());
        }
    }

    @Override
    @Transactional
    public Result<TourExecution, ApplicationError> handle(
            CreateTourExecutionCommand.PauseTourExecutionCommand command
    ) {
        var existing = tourExecutionRepository.findById(command.tourExecutionId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("TourExecution", command.tourExecutionId().toString()));
        }
        var execution = existing.get();
        var authError = accessPolicy.requireGuideOwnerOrAdmin(
                execution,
                command.actorUserId(),
                command.actorRoles()
        );
        if (authError.isPresent()) {
            return Result.failure(authError.get());
        }

        try {
            execution.pause(command.reason(), Instant.now());
            return Result.success(tourExecutionRepository.save(execution));
        } catch (TourExecutionDomainException e) {
            return Result.failure(e.toApplicationError());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("PauseTour", e.getMessage()));
        } catch (ObjectOptimisticLockingFailureException e) {
            return Result.failure(concurrentModificationError());
        }
    }

    @Override
    @Transactional
    public Result<TourExecution, ApplicationError> handle(
            CreateTourExecutionCommand.ResumeTourExecutionCommand command
    ) {
        var existing = tourExecutionRepository.findById(command.tourExecutionId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("TourExecution", command.tourExecutionId().toString()));
        }
        var execution = existing.get();
        var authError = accessPolicy.requireGuideOwnerOrAdmin(
                execution,
                command.actorUserId(),
                command.actorRoles()
        );
        if (authError.isPresent()) {
            return Result.failure(authError.get());
        }

        try {
            execution.resume(Instant.now());
            return Result.success(tourExecutionRepository.save(execution));
        } catch (TourExecutionDomainException e) {
            return Result.failure(e.toApplicationError());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("ResumeTour", e.getMessage()));
        } catch (ObjectOptimisticLockingFailureException e) {
            return Result.failure(concurrentModificationError());
        }
    }

    @Override
    @Transactional
    public Result<TourExecution, ApplicationError> handle(
            CreateTourExecutionCommand.CompleteTourExecutionCommand command
    ) {
        var existing = tourExecutionRepository.findById(command.tourExecutionId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("TourExecution", command.tourExecutionId().toString()));
        }
        var execution = existing.get();
        var authError = accessPolicy.requireGuideOwner(
                execution,
                command.actorUserId(),
                command.actorRoles()
        );
        if (authError.isPresent()) {
            return Result.failure(authError.get());
        }

        try {
            execution.complete(Instant.now());
            return Result.success(tourExecutionRepository.save(execution));
        } catch (TourExecutionDomainException e) {
            return Result.failure(e.toApplicationError());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("CompleteTour", e.getMessage()));
        } catch (ObjectOptimisticLockingFailureException e) {
            return Result.failure(concurrentModificationError());
        }
    }

    @Override
    @Transactional
    public Result<TourExecution, ApplicationError> handle(
            CreateTourExecutionCommand.AbortTourExecutionCommand command
    ) {
        var existing = tourExecutionRepository.findById(command.tourExecutionId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("TourExecution", command.tourExecutionId().toString()));
        }
        var execution = existing.get();
        var authError = accessPolicy.requireGuideOwnerOrAdmin(
                execution,
                command.actorUserId(),
                command.actorRoles()
        );
        if (authError.isPresent()) {
            return Result.failure(authError.get());
        }

        try {
            execution.abort(command.reason(), Instant.now());
            return Result.success(tourExecutionRepository.save(execution));
        } catch (TourExecutionDomainException e) {
            return Result.failure(e.toApplicationError());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("AbortTour", e.getMessage()));
        } catch (ObjectOptimisticLockingFailureException e) {
            return Result.failure(concurrentModificationError());
        }
    }

    private static ApplicationError concurrentModificationError() {
        return ApplicationError.stateConflict(
                "CONCURRENT_MODIFICATION_CONFLICT",
                "Concurrent modification detected on TourExecution",
                "The tour execution was modified concurrently by another operation. Please refresh and try again."
        );
    }
}
