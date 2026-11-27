package axiom.daos.tourmate.platform.tourmonitoring.domain.repositories;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.Participant;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository {
    Optional<Participant> findById(Long id);
    List<Participant> findAll();
    boolean existsById(Long id);
    void deleteById(Long id);
    Participant save(Participant participant);
}
