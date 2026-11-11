package axiom.daos.tourmate.platform.tour_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Currency;
import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Money;
import axiom.daos.tourmate.platform.tour_management.domain.model.commands.UpdateTourCommand;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.DifficultyLevel;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourDetails;
import axiom.daos.tourmate.platform.tour_management.interfaces.rest.resources.UpdateTourResource;

public class UpdateTourCommandFromResourceAssembler {

    public static UpdateTourCommand toCommandFromResource(Long tourId,UpdateTourResource resource) {
        return new UpdateTourCommand(
                tourId,
                new TourDetails(
                        resource.details().title(),
                        resource.details().description(),
                        resource.details().duration(),
                        DifficultyLevel.valueOf(resource.details().difficultyLevel()),
                        new Money(resource.details().price().amount(), new Currency(resource.details().price().currency()))
                ));
    }

}
