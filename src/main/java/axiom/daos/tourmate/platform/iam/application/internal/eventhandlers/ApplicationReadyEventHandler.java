package axiom.daos.tourmate.platform.iam.application.internal.eventhandlers;

import axiom.daos.tourmate.platform.iam.application.commandservices.RoleCommandService;
import axiom.daos.tourmate.platform.iam.application.commandservices.UserCommandService;
import axiom.daos.tourmate.platform.iam.domain.model.commands.ResetAllUsersPasswordCommand;
import axiom.daos.tourmate.platform.iam.domain.model.commands.SeedRolesCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * Application lifecycle handler that ensures IAM roles are seeded and existing users' passwords
 * are aligned when the application is ready.
 */
@Service
@Slf4j
public class ApplicationReadyEventHandler {
    private final RoleCommandService roleCommandService;
    private final UserCommandService userCommandService;

    public ApplicationReadyEventHandler(
            RoleCommandService roleCommandService,
            UserCommandService userCommandService
    ) {
        this.roleCommandService = roleCommandService;
        this.userCommandService = userCommandService;
    }

    /**
     * Handles the Spring application-ready event and triggers role seeding verification
     * and existing user password reset to {@code 12345678}.
     *
     * @param event Spring Boot readiness event
     */
    @EventListener
    public void on(ApplicationReadyEvent event) {
        var applicationName = event.getApplicationContext().getId();
        log.info("Starting to verify if roles seeding is needed for {} at {}", applicationName, currentTimestamp());
        var seedRolesCommand = new SeedRolesCommand();
        roleCommandService.handle(seedRolesCommand);
        var resetResult = userCommandService.handle(new ResetAllUsersPasswordCommand("12345678"));
        resetResult.toOptional().ifPresent(count ->
                log.info("Reset passwords to default (12345678) for {} existing users at {}", count, currentTimestamp())
        );
        log.info("Roles seeding verification finished for {} at {}", applicationName, currentTimestamp());
    }

    private Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
