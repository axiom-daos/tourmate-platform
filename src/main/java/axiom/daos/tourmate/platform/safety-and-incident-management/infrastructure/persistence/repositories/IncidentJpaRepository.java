package axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.repositories;

import axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.entities.IncidentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IncidentJpaRepository extends JpaRepository<IncidentJpaEntity, Long> {
    Optional<IncidentJpaEntity> findByUuid(String uuid);
}