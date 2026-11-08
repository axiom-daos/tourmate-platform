package axiom.daos.tourmate.platform.tour_management.domain.model.aggregates;

import axiom.daos.tourmate.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import axiom.daos.tourmate.platform.tour_management.domain.model.entities.Checkpoint;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.AgencyId;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.CheckpointId;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourDetails;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourStatus;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Tour extends AbstractDomainAggregateRoot<Tour> {

    private Long id;
    private AgencyId agencyId;
    private TourDetails tourDetails;
    private List<Checkpoint> checkpoints;
    private TourStatus status;

    public Tour(AgencyId agencyId, TourDetails tourDetails) {

        if(agencyId == null) {
            throw new IllegalArgumentException("Agency id cannot be null");
        }

        if(tourDetails == null) {
            throw new IllegalArgumentException("Tour details cannot be null");
        }

        this.agencyId = agencyId;
        this.tourDetails = tourDetails;
        this.checkpoints = new ArrayList<>();
        this.status = TourStatus.DRAFT;
    }

    public void publish() {
        if (this.status == TourStatus.PUBLISHED) {
            throw new IllegalStateException("Tour is already published");
        }
        if (this.checkpoints.isEmpty()) {
            throw new IllegalStateException("Cannot publish a tour without checkpoints");
        }
        this.status = TourStatus.PUBLISHED;
    }

    public void makeUnavailable() {
        if (this.status == TourStatus.UNAVAILABLE) {
            throw new IllegalStateException("Tour is already unavailable");
        }
        this.status = TourStatus.UNAVAILABLE;
    }

    public void changeDetails(TourDetails newDetails) {
        if(newDetails == null) {
            throw new IllegalArgumentException("New details cannot be null");
        }

        this.tourDetails = newDetails;
    }

    public void addCheckpoint(Checkpoint newCheckpoint) {
        if(newCheckpoint == null) {
            throw new IllegalArgumentException("New checkpoint cannot be null");
        }

        int order = newCheckpoint.getOrder();
        int maxOrder = this.checkpoints.size() + 1;

        if(order < 1 || order > maxOrder) {
            throw new IllegalArgumentException("Order must be between 1 and " + maxOrder);
        }

        this.checkpoints.add(order - 1, newCheckpoint);

        for (int i = 0; i < this.checkpoints.size(); i++) {
            this.checkpoints.get(i).changeOrder(i + 1);
        }
    }

    public void removeCheckpoint(CheckpointId checkpointId) {
        if(checkpointId == null) {
            throw new IllegalArgumentException("Checkpoint id cannot be null");
        }

        boolean removed = this.checkpoints.removeIf(c -> checkpointId.value().equals(c.getId()));

        if (!removed) {
            throw new IllegalArgumentException("Checkpoint not found in this tour");
        }

        for(int i = 0; i < this.checkpoints.size(); i++) {
            this.checkpoints.get(i).changeOrder(i + 1);
        }
    }
}
