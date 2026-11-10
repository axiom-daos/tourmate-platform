package axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects;

import org.jspecify.annotations.NullMarked;

/**
 * Value object representing a active tour identifier.
 *
 * <p>
 * This value object encapsulates the identity of a activeTour aggregate,
 * to reference activeTours strictly by identity.
 * </p>
 *
 * @param activeTourId The activeTour identifier. Cannot be null or less than 1.
 */
@NullMarked
public record ActiveTourId(Long activeTourId) {

    /**
     * Compact constructor for {@link ActiveTourId}.
     *
     * @param activeTourId The activeTour identifier.
     * @throws IllegalArgumentException if {@code activeTourId} is null or less than 1.
     */
    public ActiveTourId {
        if (activeTourId == null || activeTourId < 1) {
            throw new IllegalArgumentException("ActiveTour id cannot be null or less than 1");
        }
    }
}