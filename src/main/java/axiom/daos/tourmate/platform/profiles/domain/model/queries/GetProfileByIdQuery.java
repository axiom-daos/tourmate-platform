package axiom.daos.tourmate.platform.profiles.domain.model.queries;

/**
 * Query to retrieve a profile by its unique identifier.
 *
 * @param profileId The profile identifier.
 */
public record GetProfileByIdQuery(Long profileId) {
    public GetProfileByIdQuery {
        if (profileId == null || profileId < 1) {
            throw new IllegalArgumentException("Profile id cannot be null or less than 1");
        }
    }
}
