package axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects;

import org.jspecify.annotations.NullMarked;

/**
 * Value object representing a tour guide identifier.
 *
 * <p>
 * This value object encapsulates the identity of a tourGuide aggregate,
 * to reference tour guides strictly by identity.
 * </p>
 *
 * @param tourGuideId The tourGuide identifier. Cannot be null or less than 1.
 */
@NullMarked
public record TourGuideId(Long tourGuideId) {

    /**
     * Compact constructor for {@link TourGuideId}.
     *
     * @param tourGuideId The tourGuide identifier.
     * @throws IllegalArgumentException if {@code tourGuideId} is null or less than 1.
     */
    public TourGuideId {
        if (tourGuideId == null || tourGuideId < 1) {
            throw new IllegalArgumentException("TourGuide id cannot be null or less than 1");
        }
    }
}