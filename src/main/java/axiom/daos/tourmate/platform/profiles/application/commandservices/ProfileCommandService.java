package axiom.daos.tourmate.platform.profiles.application.commandservices;

import axiom.daos.tourmate.platform.profiles.domain.model.aggregates.Profile;
import axiom.daos.tourmate.platform.profiles.domain.model.commands.CreateProfileCommand;
import axiom.daos.tourmate.platform.profiles.domain.model.commands.DeleteProfileCommand;
import axiom.daos.tourmate.platform.profiles.domain.model.commands.UpdateProfileCommand;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;

/**
 * Application service contract for profile write commands.
 */
public interface ProfileCommandService {

    Result<Profile, ApplicationError> handle(CreateProfileCommand command);

    Result<Profile, ApplicationError> handle(UpdateProfileCommand command);

    Result<Long, ApplicationError> handle(DeleteProfileCommand command);
}
