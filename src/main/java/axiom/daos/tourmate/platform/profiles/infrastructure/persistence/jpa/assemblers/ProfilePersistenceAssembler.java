package axiom.daos.tourmate.platform.profiles.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.profiles.domain.model.aggregates.Profile;
import axiom.daos.tourmate.platform.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;

/**
 * Static assembler between domain {@link Profile} aggregates and {@link ProfilePersistenceEntity}.
 */
public final class ProfilePersistenceAssembler {

    private ProfilePersistenceAssembler() {
    }

    public static Profile toDomainFromPersistence(ProfilePersistenceEntity entity) {
        if (entity == null) return null;
        return Profile.reconstitute(
                entity.getId(),
                PersonNamePersistenceAssembler.toDomainFromPersistence(entity.getName()),
                entity.getEmail(),
                entity.getUserId()
        );
    }

    public static ProfilePersistenceEntity toPersistenceFromDomain(Profile profile) {
        if (profile == null) return null;
        var entity = new ProfilePersistenceEntity();
        // Only set ID if the profile is being updated (has a non-null ID)
        // For new profiles, leave ID null to allow JPA to generate it
        if (profile.getId() != null) {
            entity.setId(profile.getId());
        }
        entity.setName(PersonNamePersistenceAssembler.toPersistenceFromDomain(profile.getName()));
        entity.setEmail(profile.getEmail());
        entity.setUserId(profile.getUserId());
        return entity;
    }
}
