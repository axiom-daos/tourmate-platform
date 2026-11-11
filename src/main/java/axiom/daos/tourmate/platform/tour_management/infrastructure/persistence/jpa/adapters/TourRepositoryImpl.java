package axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.adapters;

import axiom.daos.tourmate.platform.tour_management.domain.model.aggregates.Tour;
import axiom.daos.tourmate.platform.tour_management.domain.repositories.TourRepository;
import axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.assemblers.TourPersistenceAssembler;
import axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.repositories.TourPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TourRepositoryImpl implements TourRepository {

    private final TourPersistenceRepository tourPersistenceRepository;

    public TourRepositoryImpl(TourPersistenceRepository tourPersistenceRepository) {
        this.tourPersistenceRepository = tourPersistenceRepository;
    }

    @Override
    public Optional<Tour> findById(Long id) {
        return this.tourPersistenceRepository.findById(id).map(TourPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Tour> findAll() {
        return this.tourPersistenceRepository.findAll().stream().map(TourPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return this.tourPersistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByTitle(String title) {
        return this.tourPersistenceRepository.existsByTourDetailsTitle(title);
    }

    @Override
    public Tour save(Tour tour) {
        var saved = this.tourPersistenceRepository.save(TourPersistenceAssembler.toPersistenceFromDomain(tour));
        return TourPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public void deleteById(Long id) {
        this.tourPersistenceRepository.deleteById(id);
    }
}
