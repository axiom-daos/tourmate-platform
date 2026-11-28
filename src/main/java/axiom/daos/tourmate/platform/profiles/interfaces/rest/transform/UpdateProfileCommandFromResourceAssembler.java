package axiom.daos.tourmate.platform.profiles.interfaces.rest.transform;

import axiom.daos.tourmate.platform.profiles.domain.model.commands.UpdateProfileCommand;
import axiom.daos.tourmate.platform.profiles.interfaces.rest.resources.UpdateProfileResource;

/**
 * Assembler that translates {@link UpdateProfileResource} into {@link UpdateProfileCommand}.
 */
public final class UpdateProfileCommandFromResourceAssembler {

    private UpdateProfileCommandFromResourceAssembler() {
    }

    public static UpdateProfileCommand toCommandFromResource(Long profileId, UpdateProfileResource resource) {
        return new UpdateProfileCommand(
                profileId,
                resource.firstName(),
                resource.lastName(),
                resource.email()
        );
    }
}
