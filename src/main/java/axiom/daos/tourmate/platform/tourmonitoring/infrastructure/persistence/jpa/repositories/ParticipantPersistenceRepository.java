package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.repositories;

import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.entities.ParticipantPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantPersistenceRepository extends JpaRepository<ParticipantPersistenceEntity, Long> {
}
