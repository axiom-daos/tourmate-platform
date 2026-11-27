package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.entities;

import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ProfileId;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourScheduleId;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.converters.ProfileIdPersistenceConverter;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.converters.TourScheduleIdPersistenceConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity(name = "TourMonitoringParticipant")
@Table(name = "tour_monitoring_participants")
@Getter
@Setter
@NoArgsConstructor
public class ParticipantPersistenceEntity extends AuditableAbstractPersistenceEntity {
    
    @Convert(converter = TourScheduleIdPersistenceConverter.class)
    @Column(name = "tour_schedule_id", nullable = false)
    private TourScheduleId tourScheduleId;

    @Convert(converter = ProfileIdPersistenceConverter.class)
    @Column(name = "profile_id", nullable = false)
    private ProfileId profileId;

    @Column(name = "joined_at", nullable = false)
    private String joinedAt;
}
