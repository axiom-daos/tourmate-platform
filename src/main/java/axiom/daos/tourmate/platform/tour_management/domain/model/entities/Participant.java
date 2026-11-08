package axiom.daos.tourmate.platform.tour_management.domain.model.entities;

import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.UserId;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
public class Participant {

    private final UserId id;
    private final LocalDateTime joinedAt;

    public Participant(UserId id) {

        if(id == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }

        this.id = id;
        this.joinedAt = LocalDateTime.now();
    }

}
