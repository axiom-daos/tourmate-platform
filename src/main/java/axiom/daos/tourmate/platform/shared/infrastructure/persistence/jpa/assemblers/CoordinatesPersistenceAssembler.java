package axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Coordinates;
import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.embeddables.CoordinatesPersistenceEmbeddable;

public class CoordinatesPersistenceAssembler {

    public static Coordinates toDomainFromPersistence(CoordinatesPersistenceEmbeddable embeddable) {

        return new Coordinates(embeddable.getX(), embeddable.getY());
    }

    public static CoordinatesPersistenceEmbeddable toPersistenceFromDomain(Coordinates coordinates) {
        return new CoordinatesPersistenceEmbeddable(coordinates.x(), coordinates.y());
    }
}
