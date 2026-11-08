package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.repositories;

import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.entities.ActiveTourPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActiveTourPersistenceRepository extends JpaRepository<ActiveTourPersistenceEntity, Long> {


}
