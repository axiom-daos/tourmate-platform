package axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.assemblers.CoordinatesPersistenceAssembler;
import axiom.daos.tourmate.platform.tour_management.domain.model.entities.Checkpoint;
import axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.entities.CheckpointPersistenceEntity;

public class CheckpointPersistenceAssembler {

    private CheckpointPersistenceAssembler(){}

    public static Checkpoint toDomainFromPersistence(CheckpointPersistenceEntity entity) {
        if(entity == null) return null;

        return Checkpoint.reconstitute(
                entity.getId(),
                entity.getName(),
                CoordinatesPersistenceAssembler.toDomainFromPersistence(entity.getLocation()),
                entity.getOrder(),
                entity.getDescription());
    }

    public static CheckpointPersistenceEntity toPersistenceFromDomain(Checkpoint checkpoint) {
        if(checkpoint == null) return null;

        var entity = new CheckpointPersistenceEntity();

        if(checkpoint.getId() != null) {
            entity.setId(checkpoint.getId());
        }

        entity.setName(checkpoint.getName());
        entity.setLocation(CoordinatesPersistenceAssembler.toPersistenceFromDomain(checkpoint.getLocation()));
        entity.setOrder(checkpoint.getOrder());
        entity.setDescription(checkpoint.getDescription());

        return entity;
    }
}
