package axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record ProfileId(Long profileId) {

    /**
     * Compact constructor for {@link ProfileId}.
     *
     * @param profileId The profile identifier.
     * @throws IllegalArgumentException if {@code profileId} is null or less than 1.
     */
    public ProfileId {
        if (profileId == null || profileId < 1) {
            throw new IllegalArgumentException("Profile id cannot be null or less than 1");
        }
    }
}