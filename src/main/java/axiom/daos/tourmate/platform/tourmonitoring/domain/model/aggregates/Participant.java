package axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates;

import axiom.daos.tourmate.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.CreateParticipantCommand;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ProfileId;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourScheduleId;
import io.jsonwebtoken.lang.Strings;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Participant extends AbstractDomainAggregateRoot<Participant> {
    private Long id;
    private TourScheduleId tourScheduleId;
    private ProfileId profileId;
    private String joinedAt;

    public Participant(){
        this.joinedAt = Strings.EMPTY;
    }

    public Participant(TourScheduleId tourScheduleId, ProfileId profileId){
        this.tourScheduleId = Objects.requireNonNull(tourScheduleId, "tourScheduleId is required");
        this.profileId = Objects.requireNonNull(profileId, "profileId is required");
        this.joinedAt = Strings.EMPTY;
    }

    public static Participant reconstitute(
            Long id,
            TourScheduleId tourScheduleId,
            ProfileId profileId,
            String joinedAt
    ){
        var participant = new Participant(tourScheduleId,profileId);
        participant.id=id;
        participant.joinedAt=joinedAt;
        return participant;
    }

    public Participant(
            Long tourScheduleId,
            Long profileId,
            String joinedAt){
        this();
        this.tourScheduleId = new TourScheduleId(tourScheduleId);
        this.profileId = new ProfileId(profileId);
        this.joinedAt = joinedAt != null ? joinedAt : Strings.EMPTY;
    }

    public Participant(CreateParticipantCommand command){
        this(
                command.tourScheduleId(),
                command.profileId(),
                command.joinedAt());
    }

}
