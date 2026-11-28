package axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ParticipantAttendanceStatus;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain entity tracking attendance and check-in status of a participant in a {@code TourExecution}.
 */
@Getter
public class ParticipantStatus {

    private final Long participantId;
    private final Long touristId;
    private final Long bookingId;
    private ParticipantAttendanceStatus status;
    private Instant checkedInAt;

    public ParticipantStatus(Long participantId, Long touristId, Long bookingId) {
        if (participantId == null || participantId < 1) {
            throw new IllegalArgumentException("Participant ID must be greater than 0");
        }
        if (touristId == null || touristId < 1) {
            throw new IllegalArgumentException("Tourist ID must be greater than 0");
        }
        this.participantId = participantId;
        this.touristId = touristId;
        this.bookingId = bookingId;
        this.status = ParticipantAttendanceStatus.PENDING;
        this.checkedInAt = null;
    }

    public static ParticipantStatus reconstitute(
            Long participantId,
            Long touristId,
            Long bookingId,
            ParticipantAttendanceStatus status,
            Instant checkedInAt
    ) {
        var ps = new ParticipantStatus(participantId, touristId, bookingId);
        ps.status = Objects.requireNonNull(status, "Participant attendance status is required");
        ps.checkedInAt = checkedInAt;
        return ps;
    }

    public void checkIn(Instant timestamp) {
        this.status = ParticipantAttendanceStatus.PRESENT;
        this.checkedInAt = Objects.requireNonNull(timestamp, "Check-in timestamp is required");
    }

    public void markAbsentIfPending() {
        if (this.status == ParticipantAttendanceStatus.PENDING) {
            this.status = ParticipantAttendanceStatus.ABSENT;
        }
    }

    public boolean isPresent() {
        return this.status == ParticipantAttendanceStatus.PRESENT;
    }
}
