package axiom.daos.tourmate.platform.profiles.interfaces.rest.transform;

import axiom.daos.tourmate.platform.profiles.domain.model.aggregates.Profile;
import axiom.daos.tourmate.platform.profiles.interfaces.rest.resources.ProfileResource;

/**
 * Assembler that converts {@link Profile} domain aggregates into {@link ProfileResource} REST objects.
 */
public final class ProfileResourceFromEntityAssembler {

    private ProfileResourceFromEntityAssembler() {
    }

    public static ProfileResource toResourceFromEntity(Profile entity) {
        return new ProfileResource(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmailAddress(),
                entity.getUserId().userId()
        );
    }
}
