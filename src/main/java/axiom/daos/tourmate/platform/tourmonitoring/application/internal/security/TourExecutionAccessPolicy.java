package axiom.daos.tourmate.platform.tourmonitoring.application.internal.security;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.tourmonitoring.domain.model.aggregates.TourExecution;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Enforces role-based and resource-ownership authorization policies for {@link TourExecution}
 * operations, preventing Insecure Direct Object Reference (IDOR) vulnerabilities.
 */
@Component
public class TourExecutionAccessPolicy {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_GUIDE = "ROLE_TOUR_GUIDE";
    public static final String ROLE_TOURIST = "ROLE_USER";

    private static final Map<Long, Long> USER_ID_TO_TOUR_GUIDE_ID = Map.of(
            5L, 1L,
            6L, 2L,
            7L, 3L,
            8L, 4L
    );

    public boolean matchesGuideIdentity(Long executionGuideId, Long actorUserId) {
        if (executionGuideId == null || actorUserId == null) {
            return false;
        }
        if (executionGuideId.equals(actorUserId)) {
            return true;
        }
        Long mappedTourGuideId = USER_ID_TO_TOUR_GUIDE_ID.get(actorUserId);
        return mappedTourGuideId != null && executionGuideId.equals(mappedTourGuideId);
    }

    public Long resolveTourGuideId(Long actorUserId) {
        if (actorUserId == null) {
            return null;
        }
        return USER_ID_TO_TOUR_GUIDE_ID.get(actorUserId);
    }

    public boolean isAdmin(Set<String> roles) {
        return roles != null && (roles.contains(ROLE_ADMIN) || roles.contains("ADMIN"));
    }

    public boolean isGuide(Set<String> roles) {
        return roles != null && (roles.contains(ROLE_GUIDE) || roles.contains("ROLE_GUIDE")
                || roles.contains("GUIDE") || roles.contains("TOUR_GUIDE"));
    }

    public boolean isTourist(Set<String> roles) {
        return roles != null && (roles.contains(ROLE_TOURIST) || roles.contains("ROLE_TOURIST")
                || roles.contains("TOURIST") || roles.contains("USER"));
    }

    public Optional<ApplicationError> requireAdmin(Set<String> roles) {
        if (isAdmin(roles)) {
            return Optional.empty();
        }
        return Optional.of(ApplicationError.forbidden("admin-monitoring", "Administrator role is required"));
    }

    public Optional<ApplicationError> requireGuideOwner(
            TourExecution execution,
            Long actorUserId,
            Set<String> roles
    ) {
        if (!isGuide(roles)) {
            return Optional.of(ApplicationError.forbidden("tour-execution", "Guide role is required"));
        }
        if (!matchesGuideIdentity(execution.getGuideId(), actorUserId)) {
            return Optional.of(ApplicationError.forbidden(
                    "tour-execution",
                    "Only the assigned guide (#%d) can operate this tour execution".formatted(execution.getGuideId())
            ));
        }
        return Optional.empty();
    }

    public Optional<ApplicationError> requireGuideOwnerOrAdmin(
            TourExecution execution,
            Long actorUserId,
            Set<String> roles
    ) {
        if (isAdmin(roles)) {
            return Optional.empty();
        }
        return requireGuideOwner(execution, actorUserId, roles);
    }

    public Optional<ApplicationError> requireTourParticipantOrGuideOrAdmin(
            TourExecution execution,
            Long actorUserId,
            Set<String> roles
    ) {
        if (isAdmin(roles)) {
            return Optional.empty();
        }
        if (isGuide(roles) && matchesGuideIdentity(execution.getGuideId(), actorUserId)) {
            return Optional.empty();
        }
        if (isTourist(roles) && actorUserId != null && execution.hasParticipantTourist(actorUserId)) {
            return Optional.empty();
        }
        return Optional.of(ApplicationError.forbidden(
                "tour-execution",
                "User is not authorized to access or report on tour execution #" + execution.getId()
        ));
    }

    public String resolvePrimaryRoleName(Set<String> roles) {
        if (isAdmin(roles)) return "ADMIN";
        if (isGuide(roles)) return "GUIDE";
        return "TOURIST";
    }
}
