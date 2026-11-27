package axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record ParticipantId(Long participantId) {

    /**
     * Compact constructor for {@link ParticipantId}.
     *
     * @param participantId The participant identifier.
     * @throws IllegalArgumentException if {@code participantId} is null or less than 1.
     */
    public ParticipantId {
        if (participantId == null || participantId < 1) {
            throw new IllegalArgumentException("Participant id cannot be null or less than 1");
        }
    }
}
