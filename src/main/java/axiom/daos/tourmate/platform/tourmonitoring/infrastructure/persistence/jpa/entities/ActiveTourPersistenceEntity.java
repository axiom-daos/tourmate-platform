package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.entities;

import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourGuideId;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourScheduleId;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.converters.TourGuideIdPersistenceConverter;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.converters.TourScheduleIdPersistenceConverter;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "active_tours")
@Getter
@Setter
@NoArgsConstructor
public class ActiveTourPersistenceEntity extends AuditableAbstractPersistenceEntity {


    @Convert(converter = TourScheduleIdPersistenceConverter.class)
    @Column(name = "tour_schedule_id", nullable = false)
    private TourScheduleId tourScheduleId;

    @Convert(converter = TourGuideIdPersistenceConverter.class)
    @Column(name = "tour_guide_id", nullable = false)
    private TourGuideId tourGuideId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private double currentLatitude;

    @Column(nullable = false)
    private double currentLongitude;

    @Column(nullable = false)
    private String startedAt;

    @Column(nullable = false)
    private String finishedAt;

}
