package axiom.daos.tourmate.platform.profiles.domain.model.valueobjects;

import org.jspecify.annotations.NullMarked;

/**
 * Value object representing an IAM user identifier referenced by a profile.
 *
 * <p>Encapsulates the identity of a user aggregate to relate profiles to users strictly by identity.</p>
 *
 * @param userId The user identifier. Cannot be null or less than 1.
 */
@NullMarked
public record UserId(Long userId) {

    public UserId {
        if (userId == null || userId < 1) {
            throw new IllegalArgumentException("User id cannot be null or less than 1");
        }
    }
}
