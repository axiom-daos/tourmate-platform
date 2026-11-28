package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.repositories;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.RiskLevel;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourStatus;
import axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.entities.TourExecutionPersistenceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link TourExecutionPersistenceEntity}.
 */
@Repository
public interface TourExecutionPersistenceRepository extends JpaRepository<TourExecutionPersistenceEntity, Long> {

    @Query("""
            SELECT te FROM TourExecutionPersistenceEntity te
            WHERE (:status IS NULL OR te.status = :status)
              AND (:status IS NOT NULL OR te.status IN :activeStatuses)
              AND (:riskLevel IS NULL OR te.riskLevel = :riskLevel)
              AND (:guideId IS NULL OR te.guideId = :guideId)
              AND (:fromDate IS NULL OR te.scheduledStartAt >= :fromDate)
              AND (:toDate IS NULL OR te.scheduledStartAt <= :toDate)
            ORDER BY te.scheduledStartAt DESC
            """)
    Page<TourExecutionPersistenceEntity> findFilteredActiveExecutions(
            @Param("status") TourStatus status,
            @Param("activeStatuses") Collection<TourStatus> activeStatuses,
            @Param("riskLevel") RiskLevel riskLevel,
            @Param("guideId") Long guideId,
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate,
            Pageable pageable
    );

    Optional<TourExecutionPersistenceEntity> findFirstByGuideIdAndStatusInOrderByScheduledStartAtDesc(
            Long guideId,
            Collection<TourStatus> statuses
    );

    @Query("""
            SELECT DISTINCT te FROM TourExecutionPersistenceEntity te
            JOIN te.participants p
            WHERE (p.touristId = :touristId OR p.participantId = :touristId)
            ORDER BY te.scheduledStartAt DESC
            """)
    List<TourExecutionPersistenceEntity> findByParticipantTouristId(@Param("touristId") Long touristId);
}
