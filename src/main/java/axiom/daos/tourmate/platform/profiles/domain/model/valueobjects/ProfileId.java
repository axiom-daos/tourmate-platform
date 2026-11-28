package axiom.daos.tourmate.platform.profiles.domain.model.valueobjects;

import org.jspecify.annotations.NullMarked;

/**
 * Value object representing a profile unique identifier.
 *
 * @param profileId The profile identifier. Cannot be null or less than 1.
 */
@NullMarked
public record ProfileId(Long profileId) {

    public ProfileId {
        if (profileId == null || profileId < 1) {
            throw new IllegalArgumentException("Profile id cannot be null or less than 1");
        }
    }
}
