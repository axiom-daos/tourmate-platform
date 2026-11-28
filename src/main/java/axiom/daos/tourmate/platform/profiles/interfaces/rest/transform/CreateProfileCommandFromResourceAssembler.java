package axiom.daos.tourmate.platform.profiles.interfaces.rest.transform;

import axiom.daos.tourmate.platform.profiles.domain.model.commands.CreateProfileCommand;
import axiom.daos.tourmate.platform.profiles.interfaces.rest.resources.CreateProfileResource;

/**
 * Assembler that translates {@link CreateProfileResource} into {@link CreateProfileCommand}.
 */
public final class CreateProfileCommandFromResourceAssembler {

    private CreateProfileCommandFromResourceAssembler() {
    }

    public static CreateProfileCommand toCommandFromResource(CreateProfileResource resource) {
        return new CreateProfileCommand(
                resource.firstName(),
                resource.lastName(),
                resource.email(),
                resource.userId()
        );
    }
}
