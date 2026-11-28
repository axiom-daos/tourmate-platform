package axiom.daos.tourmate.platform.iam.interfaces.rest;

import axiom.daos.tourmate.platform.iam.application.commandservices.UserCommandService;
import axiom.daos.tourmate.platform.iam.application.queryservices.UserQueryService;
import axiom.daos.tourmate.platform.iam.domain.model.commands.ResetAllUsersPasswordCommand;
import axiom.daos.tourmate.platform.iam.domain.model.commands.ResetUserPasswordCommand;
import axiom.daos.tourmate.platform.iam.domain.model.queries.GetAllUsersQuery;
import axiom.daos.tourmate.platform.iam.domain.model.queries.GetUserByIdQuery;
import axiom.daos.tourmate.platform.iam.interfaces.rest.resources.ResetPasswordResource;
import axiom.daos.tourmate.platform.iam.interfaces.rest.resources.SignUpResource;
import axiom.daos.tourmate.platform.iam.interfaces.rest.resources.UserResource;
import axiom.daos.tourmate.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import axiom.daos.tourmate.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller that exposes IAM user resources.
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "User management endpoints")
public class UsersController {
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    public UsersController(UserQueryService userQueryService, UserCommandService userCommandService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
    }

    /**
     * Handles user creation (sign-up) request.
     *
     * @param signUpResource the sign-up request body with username, password, and roles.
     * @return the created user resource with assigned roles.
     */
    @PostMapping
    @Operation(
        summary = "Create user (Sign-up)",
        description = "Creates a new user account with provided credentials and assigns specified roles."
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "201",
                description = "User created successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResource.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid input data or username already exists",
                content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                responseCode = "409",
                description = "Conflict - username already taken",
                content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<?> createUser(@RequestBody SignUpResource signUpResource) {
        var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        var result = userCommandService.handle(signUpCommand);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                UserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    /**
     * Retrieves all users.
     *
     * @return list of user resources
     * @see UserResource
     */
    @GetMapping
    @Operation(
        summary = "Get all users",
        description = "Retrieves a list of all users in the system with their roles.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "Users retrieved successfully",
                content = @Content(schema = @Schema(implementation = UserResource.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    public ResponseEntity<List<UserResource>> getAllUsers() {
        var getAllUsersQuery = new GetAllUsersQuery();
        var users = userQueryService.handle(getAllUsersQuery);
        var userResources = users.stream().map(UserResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(userResources);
    }

    /**
     * Retrieves a user by identifier.
     *
     * @param userId user identifier
     * @return user resource when found
     * @see UserResource
     */
    @GetMapping(value = "/{userId}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a specific user's information by unique identifier.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "User retrieved successfully",
                content = @Content(schema = @Schema(implementation = UserResource.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResource> getUserById(
            @PathVariable
            @Parameter(
                description = "Unique user identifier",
                example = "1",
                required = true
            )
            Long userId
    ) {
        var getUserByIdQuery = new GetUserByIdQuery(userId);
        var user = userQueryService.handle(getUserByIdQuery);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return ResponseEntity.ok(userResource);
    }

    /**
     * Resets a user's password by username.
     *
     * @param resource payload containing username and newPassword
     * @return updated user resource
     */
    @PostMapping("/reset-password")
    @PutMapping("/reset-password")
    @Operation(
            summary = "Reset user password by username",
            description = "Updates the password for an existing user identified by username."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> resetPasswordByUsername(
            @Valid @RequestBody ResetPasswordResource.ResetByUsernameRequest resource
    ) {
        var command = new ResetUserPasswordCommand(null, resource.username(), resource.newPassword());
        var result = userCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                UserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    /**
     * Resets a user's password by user ID.
     *
     * @param userId   target user ID
     * @param resource payload containing newPassword
     * @return updated user resource
     */
    @PutMapping("/{userId}/password")
    @Operation(
            summary = "Reset user password by user ID",
            description = "Updates the password for an existing user identified by ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> resetPasswordById(
            @PathVariable Long userId,
            @Valid @RequestBody ResetPasswordResource.ResetByIdRequest resource
    ) {
        var command = new ResetUserPasswordCommand(userId, null, resource.newPassword());
        var result = userCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                UserResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    /**
     * Resets the password of all existing users in the system (defaults to {@code 12345678}).
     *
     * @param resource optional payload with newPassword
     * @return count of updated users
     */
    @PostMapping("/reset-all-passwords")
    @Operation(
            summary = "Reset password for all existing users",
            description = "Resets the password of every existing user in the system to the specified password (defaults to 12345678)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All user passwords reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password")
    })
    public ResponseEntity<?> resetAllUsersPasswords(
            @RequestBody(required = false) ResetPasswordResource.ResetAllRequest resource
    ) {
        String password = (resource != null && resource.newPassword() != null && !resource.newPassword().isBlank())
                ? resource.newPassword()
                : "12345678";
        var result = userCommandService.handle(new ResetAllUsersPasswordCommand(password));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                count -> new ResetPasswordResource.ResetAllResponse(
                        count,
                        "Successfully reset passwords for %d users".formatted(count)
                ),
                HttpStatus.OK
        );
    }
}
