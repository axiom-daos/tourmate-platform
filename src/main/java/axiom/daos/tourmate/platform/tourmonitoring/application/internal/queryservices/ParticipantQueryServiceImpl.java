package axiom.daos.tourmate.platform.tourmonitoring.application.internal.queryservices;

import axiom.daos.tourmate.platform.tourmonitoring.application.queryservices.ParticipantQueryService;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.Participant;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.queries.GetParticipantByIdQuery;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.queries.GetAllParticipantsQuery;
import axiom.daos.tourmate.platform.tourmonitoring.domain.repositories.ParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipantQueryServiceImpl  implements ParticipantQueryService {
    private final ParticipantRepository participantRepository;

    public ParticipantQueryServiceImpl(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public Optional<Participant> handle(GetParticipantByIdQuery query) {
        return participantRepository.findById(query.participantId());
    }

    @Override
    public List<Participant> handle(GetAllParticipantsQuery query) {
        return participantRepository.findAll();
    }
}
