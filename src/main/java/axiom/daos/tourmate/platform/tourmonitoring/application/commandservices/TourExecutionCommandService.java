package axiom.daos.tourmate.platform.tourmonitoring.application.commandservices;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.TourExecution;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.commands.CreateTourExecutionCommand;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Alert;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Checkpoint;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.Incident;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.entities.ParticipantStatus;

/**
 * Application service contract for {@link TourExecution} commands.
 */
public interface TourExecutionCommandService {

    Result<TourExecution, ApplicationError> handle(CreateTourExecutionCommand command);

    Result<ParticipantStatus, ApplicationError> handle(CreateTourExecutionCommand.CheckInParticipantCommand command);

    Result<TourExecution, ApplicationError> handle(CreateTourExecutionCommand.StartTourExecutionCommand command);

    Result<Checkpoint, ApplicationError> handle(CreateTourExecutionCommand.ReachCheckpointCommand command);

    Result<Incident, ApplicationError> handle(CreateTourExecutionCommand.ReportIncidentCommand command);

    Result<Incident, ApplicationError> handle(CreateTourExecutionCommand.ResolveIncidentCommand command);

    Result<Alert, ApplicationError> handle(CreateTourExecutionCommand.TriggerSosCommand command);

    Result<TourExecution, ApplicationError> handle(CreateTourExecutionCommand.PauseTourExecutionCommand command);

    Result<TourExecution, ApplicationError> handle(CreateTourExecutionCommand.ResumeTourExecutionCommand command);

    Result<TourExecution, ApplicationError> handle(CreateTourExecutionCommand.CompleteTourExecutionCommand command);

    Result<TourExecution, ApplicationError> handle(CreateTourExecutionCommand.AbortTourExecutionCommand command);
}
