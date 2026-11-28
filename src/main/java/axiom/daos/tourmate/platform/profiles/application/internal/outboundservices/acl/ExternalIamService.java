package axiom.daos.tourmate.platform.profiles.application.internal.outboundservices.acl;

import axiom.daos.tourmate.platform.iam.interfaces.acl.IamContextFacade;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.UserId;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Outbound ACL service that bridges the Profiles bounded context with the IAM bounded context.
 */
@Service
public class ExternalIamService {

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    /**
     * Checks whether an IAM user with the given identifier exists.
     *
     * @param userId The user identifier to verify.
     * @return {@code true} if the user exists; {@code false} otherwise.
     */
    public boolean existsUserById(Long userId) {
        return iamContextFacade.existsUserById(userId);
    }

    /**
     * Fetches the IAM user identifier for a given username.
     *
     * @param username The username to search.
     * @return An {@link Optional} containing the {@link UserId}, or empty if not found.
     */
    public Optional<UserId> fetchUserIdByUsername(String username) {
        var userId = iamContextFacade.fetchUserIdByUsername(username);
        return userId == 0L ? Optional.empty() : Optional.of(new UserId(userId));
    }
}
