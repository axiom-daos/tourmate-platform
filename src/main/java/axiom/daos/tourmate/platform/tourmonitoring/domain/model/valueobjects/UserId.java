package axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects;
import org.jspecify.annotations.NullMarked;

/**
 * Value object representing a user identifier.
 *
 * <p>
 * This value object encapsulates the identity of a user aggregate,
 * to reference users strictly by identity.
 * </p>
 *
 * @param userId The user identifier. Cannot be null or less than 1.
 */
@NullMarked
public record UserId(Long userId) {

    /**
     * Compact constructor for {@link UserId}.
     *
     * @param userId The user identifier.
     * @throws IllegalArgumentException if {@code userId} is null or less than 1.
     */
    public UserId {
        if (userId == null || userId < 1) {
            throw new IllegalArgumentException("User id cannot be null or less than 1");
        }
    }
}