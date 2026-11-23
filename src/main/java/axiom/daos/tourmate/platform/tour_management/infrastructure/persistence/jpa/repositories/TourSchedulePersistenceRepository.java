package axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.repositories;

import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.DepartureDateTime;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourId;
import axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.entities.TourSchedulePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TourSchedulePersistenceRepository extends JpaRepository<TourSchedulePersistenceEntity, Long> {

    boolean existsByTourIdAndDepartureDateTime(TourId tourId, DepartureDateTime departureDateTime);
}
