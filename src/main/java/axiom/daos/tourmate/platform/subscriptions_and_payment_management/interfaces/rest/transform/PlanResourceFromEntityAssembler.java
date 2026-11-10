package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.transform;

import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Plan;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources.PlanResource;

/**
 * Assembler to map {@link Plan} entity to {@link PlanResource}.
 */
public final class PlanResourceFromEntityAssembler {

    private PlanResourceFromEntityAssembler() {
    }

    public static PlanResource toResourceFromEntity(Plan entity) {
        if (entity == null) {
            return null;
        }
        return new PlanResource(
                entity.getId(),
                entity.getName(),
                entity.getPrice().amount(),
                entity.getPrice().currency().code(),
                entity.getDescription(),
                entity.getMaxActiveTours()
        );
    }
}
