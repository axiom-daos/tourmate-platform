package axiom.daos.tourmate.platform.tour_management.domain.model.aggregates;

import axiom.daos.tourmate.platform.iam.domain.model.aggregates.User;
import axiom.daos.tourmate.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import axiom.daos.tourmate.platform.tour_management.domain.model.entities.Participant;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.DepartureDateTime;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourId;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourScheduleStatus;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.UserId;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TourSchedule extends AbstractDomainAggregateRoot<TourSchedule> {

    private Long id;
    private TourId tourId;
    private DepartureDateTime departureDateTime;
    private int maxCapacity;
    private List<Participant> participants;
    private TourScheduleStatus status;

    public TourSchedule(TourId tourId, DepartureDateTime departureDateTime, int maxCapacity) {

        if(tourId == null) {
            throw new IllegalArgumentException("Tour id cannot be null");
        }
        if(departureDateTime == null) {
            throw new IllegalArgumentException("Departure datetime cannot be null");
        }
        if(maxCapacity < 1) {
            throw new IllegalArgumentException("Max capacity must be greater than 0");
        }


        this.tourId = tourId;
        this.departureDateTime = departureDateTime;
        this.maxCapacity = maxCapacity;
        this.participants = new ArrayList<>();
        this.status = TourScheduleStatus.OPENED;
    }

    public void addParticipant(UserId userId) {
        if(userId == null) {
            throw new IllegalArgumentException("New participant cannot be null");
        }
        if (this.status != TourScheduleStatus.OPENED) {
            throw new IllegalStateException("Tour schedule is not open for registrations");
        }
        if (this.participants.size() >= this.maxCapacity) {
            throw new IllegalStateException("Tour schedule is full");
        }

        boolean alreadyRegistered = this.participants.stream().anyMatch(p -> p.getId().equals(userId));

        if (alreadyRegistered) {
            throw new IllegalArgumentException("Participant already registered");
        }

        this.participants.add(new Participant(userId));
    }

    public void cancelParticipant(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }

        boolean removed = this.participants.removeIf(participant -> participant.getId().equals(userId));

        if(!removed) {
            throw new IllegalArgumentException("Participant not found in this schedule");
        }

    }

    public void open() {
        if(this.status == TourScheduleStatus.OPENED) {
            throw new IllegalArgumentException("Tour schedule is already opened");
        }

        this.status = TourScheduleStatus.OPENED;
    }

    public void close() {
        if(this.status == TourScheduleStatus.CLOSED) {
            throw new IllegalArgumentException("Tour schedule is already closed");
        }

        this.status = TourScheduleStatus.CLOSED;
    }

    public static TourSchedule reconstitute(
            Long id,
            TourId tourId,
            DepartureDateTime departureDateTime,
            int maxCapacity,
            List<Participant> participants,
            TourScheduleStatus status) {

        var tourSchedule = new TourSchedule(tourId, departureDateTime, maxCapacity);

        tourSchedule.id = id;
        tourSchedule.participants = new ArrayList<>(participants);
        tourSchedule.status = status;

        return tourSchedule;
    }
}
