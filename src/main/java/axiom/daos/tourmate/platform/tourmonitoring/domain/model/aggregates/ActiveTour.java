package axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates;

import axiom.daos.tourmate.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.Status;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourGuideId;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourScheduleId;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

@Getter
public class ActiveTour extends AbstractDomainAggregateRoot<ActiveTour> {
    private Long id;
    private TourScheduleId tourScheduleId;
    private TourGuideId tourGuideId;
    private Status status;
    private double currentLatitude;
    private double currentLongitude;
    private String startedAt;
    private String finishedAt;

    public ActiveTour(){
       this.currentLatitude = 0.0;
       this.currentLongitude = 0.0;
       this.startedAt= Strings.EMPTY;
       this.finishedAt= Strings.EMPTY;
    }

    public ActiveTour(TourScheduleId tourScheduleId, TourGuideId tourGuideId){
        this.tourScheduleId = Objects.requireNonNull(tourScheduleId, "tourScheduleId is required");
        this.tourGuideId = Objects.requireNonNull(tourGuideId, "tourGuideId is required");
        this.status = Status.IN_PROGRESS;
        this.currentLatitude = 0.0;
        this.currentLongitude = 0.0;
        this.startedAt= Strings.EMPTY;
        this.finishedAt= Strings.EMPTY;
    }

    public static ActiveTour reconstitute(
            Long id,
            TourScheduleId tourScheduleId,
            TourGuideId tourGuideId,
            Status status,
            double currentLatitude,
            double currentLongitude,
            String startedAt,
            String finishedAt) {
        var activeTour = new ActiveTour(tourScheduleId, tourGuideId);
        activeTour.id = id;
        activeTour.status = status;
        activeTour.currentLatitude = currentLatitude;
        activeTour.currentLongitude = currentLongitude;
        activeTour.startedAt = startedAt;
        activeTour.finishedAt = finishedAt;
        return activeTour;
    }

}
