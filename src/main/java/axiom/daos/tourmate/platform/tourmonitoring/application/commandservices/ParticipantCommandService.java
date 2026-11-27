package axiom.daos.tourmate.platform.tourmonitoring.application.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.CreateParticipantCommand;

public interface ParticipantCommandService {
    Result<Long, ApplicationError> handle(CreateParticipantCommand command);
}
