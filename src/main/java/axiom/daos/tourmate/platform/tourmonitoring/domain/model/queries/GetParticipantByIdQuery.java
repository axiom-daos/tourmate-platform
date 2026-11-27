package axiom.daos.tourmate.platform.tourmonitoring.domain.model.queries;

public record GetParticipantByIdQuery(Long participantId) {
    public GetParticipantByIdQuery{
        if (participantId == null || participantId <= 0) {
            throw new IllegalArgumentException("participantId cannot be null or less than 1");
        }
    }
}
