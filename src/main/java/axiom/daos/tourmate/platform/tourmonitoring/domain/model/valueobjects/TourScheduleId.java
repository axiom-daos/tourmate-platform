package axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects;

import org.jspecify.annotations.NullMarked;

/**
 * Value object representing a tour schedule identifier.
 *
 * <p>
 * This value object encapsulates the identity of a tourSchedule aggregate,
 * to reference tourSchedules strictly by identity.
 * </p>
 *
 * @param tourScheduleId The tourSchedule identifier. Cannot be null or less than 1.
 */
@NullMarked
public record TourScheduleId(Long tourScheduleId) {

    /**
     * Compact constructor for {@link TourScheduleId}.
     *
     * @param tourScheduleId The tourSchedule identifier.
     * @throws IllegalArgumentException if {@code tourScheduleId} is null or less than 1.
     */
    public TourScheduleId {
        if (tourScheduleId == null || tourScheduleId < 1) {
            throw new IllegalArgumentException("TourSchedule id cannot be null or less than 1");
        }
    }
}