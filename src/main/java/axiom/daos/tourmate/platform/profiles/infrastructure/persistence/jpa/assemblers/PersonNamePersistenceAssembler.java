package axiom.daos.tourmate.platform.profiles.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.PersonName;
import axiom.daos.tourmate.platform.profiles.infrastructure.persistence.jpa.embeddables.PersonNamePersistenceEmbeddable;

/**
 * Static assembler between domain {@link PersonName} and {@link PersonNamePersistenceEmbeddable}.
 */
public final class PersonNamePersistenceAssembler {

    private PersonNamePersistenceAssembler() {
    }

    public static PersonName toDomainFromPersistence(PersonNamePersistenceEmbeddable embeddable) {
        if (embeddable == null) return null;
        return new PersonName(embeddable.getFirstName(), embeddable.getLastName());
    }

    public static PersonNamePersistenceEmbeddable toPersistenceFromDomain(PersonName personName) {
        if (personName == null) return null;
        return new PersonNamePersistenceEmbeddable(personName.firstName(), personName.lastName());
    }
}
