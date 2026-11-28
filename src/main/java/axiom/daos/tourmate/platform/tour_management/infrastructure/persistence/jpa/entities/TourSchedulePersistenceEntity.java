package axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.entities;

import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.DepartureDateTime;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourId;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourScheduleStatus;
import axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.converters.DepartureDateTimePersistenceConverter;
import axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.converters.TourIdPersistenceConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tour_schedules")
@Getter
@Setter
@NoArgsConstructor
public class TourSchedulePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Convert(converter = TourIdPersistenceConverter.class)
    @Column(name = "tour_id", nullable = false)
    private TourId tourId;

    @Convert(converter = DepartureDateTimePersistenceConverter.class)
    @Column(name = "departure_datetime", nullable = false)
    private DepartureDateTime departureDateTime;

    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity;

    @OneToMany(mappedBy = "tourSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipantPersistenceEntity> participants;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TourScheduleStatus status;
}
