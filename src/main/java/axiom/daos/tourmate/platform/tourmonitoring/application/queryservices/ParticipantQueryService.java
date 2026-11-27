package axiom.daos.tourmate.platform.tourmonitoring.application.queryservices;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.Participant;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.queries.GetParticipantByIdQuery;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.queries.GetAllParticipantsQuery;

import java.util.List;
import java.util.Optional;

public interface ParticipantQueryService {
    /**
     * Handles retrieval of a participant by id.
     *
     * @param query participant-id query
     * @return matching participant, if found
     * @see GetParticipantByIdQuery
     */
    Optional<Participant> handle(GetParticipantByIdQuery query);

    /**
     * Handles retrieval of all participants.
     *
     * @param query query marker
     * @return list of participants
     * @see GetAllParticipantsQuery
     */
    List<Participant> handle(GetAllParticipantsQuery query);

}
