package axiom.daos.tourmate.platform.profiles.domain.model.queries;

import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.UserId;

/**
 * Query to retrieve a profile by its associated user identifier.
 *
 * @param userId The user identifier value object.
 */
public record GetProfileByUserIdQuery(UserId userId) {
    public GetProfileByUserIdQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
    }
}
