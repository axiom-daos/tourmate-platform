package axiom.daos.tourmate.platform.iam.application.commandservices;

import axiom.daos.tourmate.platform.iam.domain.model.aggregates.User;
import axiom.daos.tourmate.platform.iam.domain.model.commands.ResetAllUsersPasswordCommand;
import axiom.daos.tourmate.platform.iam.domain.model.commands.ResetUserPasswordCommand;
import axiom.daos.tourmate.platform.iam.domain.model.commands.SignInCommand;
import axiom.daos.tourmate.platform.iam.domain.model.commands.SignUpCommand;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * Application service contract for IAM user commands.
 */
public interface UserCommandService {
    /**
     * Handles user sign-in.
     *
     * @param command sign-in command
     * @return authenticated user and token pair, or an application error
     */
    Result<ImmutablePair<User, String>, ApplicationError> handle(SignInCommand command);

    /**
     * Handles user sign-up.
     *
     * @param command sign-up command
     * @return created user aggregate, or an application error
     */
    Result<User, ApplicationError> handle(SignUpCommand command);

    /**
     * Resets the password of a specific user by ID or username.
     *
     * @param command reset user password command
     * @return updated user aggregate, or an application error
     */
    Result<User, ApplicationError> handle(ResetUserPasswordCommand command);

    /**
     * Resets the password of all existing users in the system.
     *
     * @param command reset all users password command
     * @return number of updated users, or an application error
     */
    Result<Integer, ApplicationError> handle(ResetAllUsersPasswordCommand command);
}

