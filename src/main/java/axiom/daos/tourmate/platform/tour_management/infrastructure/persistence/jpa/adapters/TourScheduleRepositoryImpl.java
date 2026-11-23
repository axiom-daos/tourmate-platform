package axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.adapters;

import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.TourSchedule;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.DepartureDateTime;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourId;
import axiom.daos.tourmate.platform.tour_management.domain.repositories.TourScheduleRepository;
import axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.assemblers.TourSchedulePersistenceAssembler;
import axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.repositories.TourSchedulePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TourScheduleRepositoryImpl implements TourScheduleRepository {

    private final TourSchedulePersistenceRepository tourSchedulePersistenceRepository;

    public TourScheduleRepositoryImpl(TourSchedulePersistenceRepository tourSchedulePersistenceRepository) {
        this.tourSchedulePersistenceRepository = tourSchedulePersistenceRepository;
    }

    @Override
    public Optional<TourSchedule> findById(Long id) {
        return this.tourSchedulePersistenceRepository.findById(id).map(TourSchedulePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<TourSchedule> findAll() {
        return this.tourSchedulePersistenceRepository.findAll().stream().map(TourSchedulePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return this.tourSchedulePersistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByTourIdAndDepartureDateTime(TourId tourId, DepartureDateTime departureDateTime) {
        return this.tourSchedulePersistenceRepository.existsByTourIdAndDepartureDateTime(tourId, departureDateTime);
    }

    @Override
    public TourSchedule save(TourSchedule tourSchedule) {
        var tourScheduleSaved = this.tourSchedulePersistenceRepository.save(TourSchedulePersistenceAssembler.toPersistenceFromDomain(tourSchedule));
        return TourSchedulePersistenceAssembler.toDomainFromPersistence(tourScheduleSaved);
    }

    @Override
    public void deleteById(Long id) {
        this.tourSchedulePersistenceRepository.deleteById(id);
    }
}
