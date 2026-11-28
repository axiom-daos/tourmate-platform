package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.TourExecution;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Alert;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Checkpoint;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Incident;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.ParticipantStatus;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.TimelineEntry;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.GeoLocation;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TimeWindow;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.entities.TourExecutionPersistenceEntity;

import java.util.List;

/**
 * Static assembler between {@link TourExecution} domain aggregates and {@link TourExecutionPersistenceEntity}.
 */
public final class TourExecutionPersistenceAssembler {

    private TourExecutionPersistenceAssembler() {
    }

    public static TourExecution toDomainFromPersistence(TourExecutionPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }
        GeoLocation currentLocation = (entity.getCurrentLatitude() != null && entity.getCurrentLongitude() != null)
                ? new GeoLocation(entity.getCurrentLatitude(), entity.getCurrentLongitude())
                : null;

        List<Checkpoint> checkpoints = entity.getCheckpoints() == null ? List.of() : entity.getCheckpoints().stream()
                .map(cp -> Checkpoint.reconstitute(
                        cp.getCheckpointId(),
                        cp.getName(),
                        cp.getSequenceOrder(),
                        new GeoLocation(cp.getLatitude(), cp.getLongitude()),
                        new TimeWindow(cp.getExpectedArrival(), cp.getLatestAllowedArrival()),
                        cp.isReached(),
                        cp.getReachedAt(),
                        cp.getDelayMinutes(),
                        cp.isDelayedBeyondTolerance()
                ))
                .toList();

        List<ParticipantStatus> participants = entity.getParticipants() == null ? List.of() : entity.getParticipants().stream()
                .map(p -> ParticipantStatus.reconstitute(
                        p.getParticipantId(),
                        p.getTouristId(),
                        p.getBookingId(),
                        p.getStatus(),
                        p.getCheckedInAt()
                ))
                .toList();

        List<Incident> incidents = entity.getIncidents() == null ? List.of() : entity.getIncidents().stream()
                .map(inc -> Incident.reconstitute(
                        inc.getIncidentId(),
                        inc.getType(),
                        inc.getSeverity(),
                        inc.getDescription(),
                        (inc.getLatitude() != null && inc.getLongitude() != null)
                                ? new GeoLocation(inc.getLatitude(), inc.getLongitude())
                                : null,
                        inc.getReportedById(),
                        inc.getReportedByRole(),
                        inc.getStatus(),
                        inc.getResolutionNotes(),
                        inc.getReportedAt(),
                        inc.getResolvedAt()
                ))
                .toList();

        List<Alert> alerts = entity.getAlerts() == null ? List.of() : entity.getAlerts().stream()
                .map(a -> new Alert(
                        a.getAlertId(),
                        a.getType(),
                        a.getSeverity(),
                        a.getMessage(),
                        a.getSourceUserId(),
                        a.getSourceRole(),
                        (a.getLatitude() != null && a.getLongitude() != null)
                                ? new GeoLocation(a.getLatitude(), a.getLongitude())
                                : null,
                        a.isActive(),
                        a.getCreatedAt()
                ))
                .toList();

        List<TimelineEntry> timeline = entity.getTimeline() == null ? List.of() : entity.getTimeline().stream()
                .map(t -> new TimelineEntry(
                        t.getEntryId(),
                        t.getEventType(),
                        t.getDescription(),
                        t.getOccurredAt()
                ))
                .toList();

        return TourExecution.reconstitute(
                entity.getId(),
                entity.getVersion(),
                entity.getTourId(),
                entity.getGuideId(),
                entity.getBookingId(),
                entity.getStatus(),
                entity.getRiskLevel(),
                currentLocation,
                entity.getScheduledStartAt(),
                entity.getStartedAt(),
                entity.getPausedAt(),
                entity.getCompletedAt(),
                entity.getAbortedAt(),
                entity.getPauseReason(),
                entity.getAbortReason(),
                checkpoints,
                participants,
                incidents,
                alerts,
                timeline
        );
    }

    public static TourExecutionPersistenceEntity toPersistenceFromDomain(TourExecution domain) {
        if (domain == null) {
            return null;
        }
        var entity = new TourExecutionPersistenceEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        if (domain.getVersion() != null) {
            entity.setVersion(domain.getVersion());
        }
        entity.setTourId(domain.getTourId());
        entity.setGuideId(domain.getGuideId());
        entity.setBookingId(domain.getBookingId());
        entity.setStatus(domain.getStatus());
        entity.setRiskLevel(domain.getRiskLevel());
        if (domain.getCurrentLocation() != null) {
            entity.setCurrentLatitude(domain.getCurrentLocation().latitude());
            entity.setCurrentLongitude(domain.getCurrentLocation().longitude());
        }
        entity.setScheduledStartAt(domain.getScheduledStartAt());
        entity.setStartedAt(domain.getStartedAt());
        entity.setPausedAt(domain.getPausedAt());
        entity.setCompletedAt(domain.getCompletedAt());
        entity.setAbortedAt(domain.getAbortedAt());
        entity.setPauseReason(domain.getPauseReason());
        entity.setAbortReason(domain.getAbortReason());

        entity.setCheckpoints(domain.getCheckpoints().stream()
                .map(cp -> new TourExecutionPersistenceEntity.CheckpointEmbeddable(
                        cp.getId(),
                        cp.getName(),
                        cp.getSequenceOrder(),
                        cp.getLocation().latitude(),
                        cp.getLocation().longitude(),
                        cp.getTimeWindow().expectedArrival(),
                        cp.getTimeWindow().latestAllowedArrival(),
                        cp.isReached(),
                        cp.getReachedAt(),
                        cp.getDelayMinutes(),
                        cp.isDelayedBeyondTolerance()
                ))
                .toList());

        entity.setParticipants(domain.getParticipants().stream()
                .map(p -> new TourExecutionPersistenceEntity.ParticipantStatusEmbeddable(
                        p.getParticipantId(),
                        p.getTouristId(),
                        p.getBookingId(),
                        p.getStatus(),
                        p.getCheckedInAt()
                ))
                .toList());

        entity.setIncidents(domain.getIncidents().stream()
                .map(inc -> new TourExecutionPersistenceEntity.IncidentEmbeddable(
                        inc.getId(),
                        inc.getType(),
                        inc.getSeverity(),
                        inc.getDescription(),
                        inc.getLocation() != null ? inc.getLocation().latitude() : null,
                        inc.getLocation() != null ? inc.getLocation().longitude() : null,
                        inc.getReportedById(),
                        inc.getReportedByRole(),
                        inc.getStatus(),
                        inc.getResolutionNotes(),
                        inc.getReportedAt(),
                        inc.getResolvedAt()
                ))
                .toList());

        entity.setAlerts(domain.getAlerts().stream()
                .map(a -> new TourExecutionPersistenceEntity.AlertEmbeddable(
                        a.getId(),
                        a.getType(),
                        a.getSeverity(),
                        a.getMessage(),
                        a.getSourceUserId(),
                        a.getSourceRole(),
                        a.getLocation() != null ? a.getLocation().latitude() : null,
                        a.getLocation() != null ? a.getLocation().longitude() : null,
                        a.isActive(),
                        a.getCreatedAt()
                ))
                .toList());

        entity.setTimeline(domain.getTimeline().stream()
                .map(t -> new TourExecutionPersistenceEntity.TimelineEntryEmbeddable(
                        t.getId(),
                        t.getEventType(),
                        t.getDescription(),
                        t.getOccurredAt()
                ))
                .toList());

        return entity;
    }
}
