package axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.repositories;

import axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.entities.IncidentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentJpaRepository extends JpaRepository<IncidentJpaEntity, Long> {
}