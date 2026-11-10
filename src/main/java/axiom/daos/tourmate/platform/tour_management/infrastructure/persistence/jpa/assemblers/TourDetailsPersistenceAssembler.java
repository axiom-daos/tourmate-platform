package axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Currency;
import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Money;
import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.embeddables.MoneyPersistenceEmbeddable;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.DifficultyLevel;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourDetails;
import axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.embeddables.TourDetailsPersistenceEmbeddable;

public class TourDetailsPersistenceAssembler {

    public static TourDetails toDomainFromPersistence(TourDetailsPersistenceEmbeddable embeddable) {
        if(embeddable == null) return null;

        return new TourDetails(
                embeddable.getTitle(),
                embeddable.getDescription(),
                embeddable.getDuration(),
                DifficultyLevel.valueOf(embeddable.getDifficultyLevel()),
                new Money(embeddable.getPrice().getAmount(), new Currency(embeddable.getPrice().getCurrency())));
    }

    public static TourDetailsPersistenceEmbeddable toPersistenceFromDomain(TourDetails tourDetails) {
        if(tourDetails == null) return null;

        return new TourDetailsPersistenceEmbeddable(
                tourDetails.title(),
                tourDetails.description(),
                tourDetails.duration(),
                tourDetails.difficultyLevel().name(),
                new MoneyPersistenceEmbeddable(tourDetails.price().amount(), tourDetails.price().currency().code()));
    }
}
