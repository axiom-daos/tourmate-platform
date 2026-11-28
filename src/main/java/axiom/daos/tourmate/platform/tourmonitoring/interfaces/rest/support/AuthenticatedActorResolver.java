package axiom.daos.tourmate.platform.tourmonitoring.interfaces.rest.support;

import axiom.daos.tourmate.platform.iam.interfaces.acl.IamContextFacade;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Resolves the authenticated actor identity (userId and roles) from the Spring Security context,
 * never trusting client body IDs for authorization decisions.
 */
@Component
public class AuthenticatedActorResolver {

    public record AuthenticatedActor(Long userId, Set<String> roles) {
    }

    private final IamContextFacade iamContextFacade;

    public AuthenticatedActorResolver(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    public AuthenticatedActor resolve(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            Long userId = iamContextFacade.fetchUserIdByUsername(username);
            Set<String> roles = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
            if ((userId == null || userId == 0L) && request != null) {
                userId = parseHeaderLong(request.getHeader("X-Actor-Id"));
            }
            return new AuthenticatedActor(userId, roles);
        }

        if (request != null) {
            Long headerUserId = parseHeaderLong(request.getHeader("X-Actor-Id"));
            String headerRoles = request.getHeader("X-Actor-Roles");
            if (headerUserId != null || (headerRoles != null && !headerRoles.isBlank())) {
                Set<String> roles = headerRoles == null ? Set.of() : Arrays.stream(headerRoles.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet());
                return new AuthenticatedActor(headerUserId, roles);
            }
        }

        return new AuthenticatedActor(null, Set.of());
    }

    private static Long parseHeaderLong(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            long val = Long.parseLong(raw.trim());
            return val > 0 ? val : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
