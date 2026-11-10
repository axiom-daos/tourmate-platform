package axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.entities;

import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.AgencyId;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourDetails;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourStatus;
import axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.converters.AgencyIdPersistenceConverter;
import axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.embeddables.TourDetailsPersistenceEmbeddable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tours")
@Getter
@Setter
@NoArgsConstructor
public class TourPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Convert(converter = AgencyIdPersistenceConverter.class)
    @Column(name = "agency_id", nullable = false)
    private Long agencyId;

    @Embedded
    private TourDetailsPersistenceEmbeddable tourDetails;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CheckpointPersistenceEntity> checkpoints;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TourStatus status;
}
