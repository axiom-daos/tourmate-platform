package axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.entities;

import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Coordinates;
import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.embeddables.CoordinatesPersistenceEmbeddable;
import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "checkpoints")
@Getter
@Setter
@NoArgsConstructor
public class CheckpointPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private TourPersistenceEntity tour;

    @Embedded
    private CoordinatesPersistenceEmbeddable location;

    @Column(name = "checkpoint_order", nullable = false)
    private int order;

    @Column(name = "description", nullable = false)
    private String description;
}
