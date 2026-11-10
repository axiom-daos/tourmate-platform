package axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.repositories;

import axiom.daos.tourmate.platform.safety_and_incident_management.infrastructure.persistence.entities.IncidentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * JPA repository interface for managing IncidentJpaEntity instances, providing CRUD operations and query methods for incident data in the database.
 */
public interface IncidentJpaRepository extends JpaRepository<IncidentJpaEntity, Long> {
}