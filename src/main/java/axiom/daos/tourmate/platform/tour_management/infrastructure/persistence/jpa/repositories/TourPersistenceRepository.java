package axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.repositories;

import axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.entities.TourPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourPersistenceRepository extends JpaRepository<TourPersistenceEntity, Long> {

    boolean existsByTourDetailsTitle(String title);
}
