package axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Currency;
import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Money;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Plan;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.infrastructure.persistence.jpa.entities.PlanPersistenceEntity;

/**
 * Static assembler between plan domain and persistence representations.
 */
public final class PlanPersistenceAssembler {

    private PlanPersistenceAssembler() {
    }

    public static Plan toDomainFromPersistence(PlanPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }
        var currency = new Currency(entity.getCurrency());
        var price = new Money(entity.getPrice(), currency);
        return Plan.reconstitute(
                entity.getId(),
                entity.getName(),
                price,
                entity.getDescription(),
                entity.getMaxActiveTours()
        );
    }

    public static PlanPersistenceEntity toPersistenceFromDomain(Plan plan) {
        if (plan == null) {
            return null;
        }
        var entity = new PlanPersistenceEntity();
        if (plan.getId() != null) {
            entity.setId(plan.getId());
        }
        entity.setName(plan.getName());
        entity.setDescription(plan.getDescription());
        entity.setPrice(plan.getPrice().amount());
        entity.setCurrency(plan.getPrice().currency().code());
        entity.setMaxActiveTours(plan.getMaxActiveTours());
        return entity;
    }
}
