package axiom.daos.tourmate.platform.tour_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Currency;
import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Money;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.CreateTourCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.AgencyId;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.DifficultyLevel;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourDetails;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.CreateTourResource;

public class CreateTourCommandFromResourceAssembler {

    public static CreateTourCommand toCommandFromResource(CreateTourResource resource) {
        return new CreateTourCommand(
                new AgencyId(resource.agencyID()),
                new TourDetails(
                        resource.details().title(),
                        resource.details().description(),
                        resource.details().duration(),
                        DifficultyLevel.valueOf(resource.details().difficultyLevel()),
                        new Money(resource.details().price().amount(), new Currency(resource.details().price().currency()))
                ));
    }
}
