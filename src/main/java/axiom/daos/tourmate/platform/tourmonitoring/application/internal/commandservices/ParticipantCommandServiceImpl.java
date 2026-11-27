package axiom.daos.tourmate.platform.tourmonitoring.application.internal.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.tourmonitoring.application.commandservices.ParticipantCommandService;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.Participant;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.CreateParticipantCommand;

import axiom.daos.tourmate.platform.tourmonitoring.domain.repositories.ParticipantRepository;
import org.springframework.stereotype.Service;

@Service
public class ParticipantCommandServiceImpl implements ParticipantCommandService {
    private final ParticipantRepository participantRepository;

    public ParticipantCommandServiceImpl(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(CreateParticipantCommand command) {

        var participant = new Participant(command);
        try {
            participant = participantRepository.save(participant);
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-participant", e.getMessage()));
        }
        return Result.success(participant.getId());
    }

}
