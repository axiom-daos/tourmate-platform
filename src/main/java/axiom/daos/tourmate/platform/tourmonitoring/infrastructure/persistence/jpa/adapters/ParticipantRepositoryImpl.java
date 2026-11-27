package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.adapters;

import axiom.daos.tourmate.platform.shared.infrastructure.events.DomainEventPublisher;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.Participant;
import axiom.daos.tourmate.platform.tourmonitoring.domain.repositories.ParticipantRepository;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.assemblers.ParticipantPersistenceAssembler;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.repositories.ParticipantPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ParticipantRepositoryImpl implements ParticipantRepository {
    
    private final ParticipantPersistenceRepository participantPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public ParticipantRepositoryImpl(
            ParticipantPersistenceRepository participantPersistenceRepository,
            DomainEventPublisher domainEventPublisher) {
        this.participantPersistenceRepository = participantPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public void deleteById(Long id) {
        participantPersistenceRepository.deleteById(id);
    }

    @Override
    public Participant save(Participant participant) {
        var saved = participantPersistenceRepository.save(ParticipantPersistenceAssembler.toPersistenceFromDomain(participant));
        var savedParticipant = ParticipantPersistenceAssembler.toDomainFromPersistence(saved);
        domainEventPublisher.publishAndClearEvents(participant);
        return savedParticipant;
    }

    @Override
    public boolean existsById(Long id) {
        return participantPersistenceRepository.existsById(id);
    }

    @Override
    public List<Participant> findAll() {
        return participantPersistenceRepository.findAll().stream().map(ParticipantPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Optional<Participant> findById(Long id) {
        return participantPersistenceRepository.findById(id).map(ParticipantPersistenceAssembler::toDomainFromPersistence);
    }
}
