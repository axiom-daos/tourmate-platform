package axiom.daos.tourmate.platform.profiles.interfaces.rest;

import axiom.daos.tourmate.platform.profiles.application.commandservices.ProfileCommandService;
import axiom.daos.tourmate.platform.profiles.application.queryservices.ProfileQueryService;
import axiom.daos.tourmate.platform.profiles.domain.model.commands.DeleteProfileCommand;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetAllProfilesQuery;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetProfileByIdQuery;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.UserId;
import axiom.daos.tourmate.platform.profiles.interfaces.rest.resources.CreateProfileResource;
import axiom.daos.tourmate.platform.profiles.interfaces.rest.resources.ProfileResource;
import axiom.daos.tourmate.platform.profiles.interfaces.rest.resources.UpdateProfileResource;
import axiom.daos.tourmate.platform.profiles.interfaces.rest.transform.CreateProfileCommandFromResourceAssembler;
import axiom.daos.tourmate.platform.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import axiom.daos.tourmate.platform.profiles.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;
import axiom.daos.tourmate.platform.shared.interfaces.rest.resources.MessageResource;
import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller that exposes profile management endpoints.
 */
@RestController
@RequestMapping(value = "/api/v1/profiles", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "Profile management endpoints")
public class ProfilesController {

    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;

    public ProfilesController(
            ProfileCommandService profileCommandService,
            ProfileQueryService profileQueryService
    ) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
    }

    /**
     * Creates a new profile linked to an existing IAM user.
     *
     * @param resource The {@link CreateProfileResource} request payload.
     * @return The created {@link ProfileResource} or an error response.
     */
    @PostMapping
    @Operation(
            summary = "Create a new profile",
            description = "Creates a new profile with first name, last name, email, and associated user identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Profile created successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Associated user not found"),
            @ApiResponse(responseCode = "409", description = "Profile already exists for email or user")
    })
    public ResponseEntity<?> createProfile(@Valid @RequestBody CreateProfileResource resource) {
        var createProfileCommand = CreateProfileCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = profileCommandService.handle(createProfileCommand);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                ProfileResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    /**
     * Retrieves all profiles.
     *
     * @return List of {@link ProfileResource} representations.
     */
    @GetMapping
    @Operation(
            summary = "Get all profiles",
            description = "Retrieves a list of all registered profiles."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profiles retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))
            )
    })
    public ResponseEntity<List<ProfileResource>> getAllProfiles() {
        var profiles = profileQueryService.handle(new GetAllProfilesQuery());
        var profileResources = profiles.stream()
                .map(ProfileResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(profileResources);
    }

    /**
     * Retrieves a profile by its unique identifier.
     *
     * @param profileId The unique profile identifier.
     * @return The {@link ProfileResource} when found, or 404 Not Found.
     */
    @GetMapping("/{profileId}")
    @Operation(
            summary = "Get profile by ID",
            description = "Retrieves a specific profile by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile found",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))
            ),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<ProfileResource> getProfileById(
            @PathVariable
            @Parameter(description = "Unique profile identifier", example = "1", required = true)
            Long profileId
    ) {
        var profile = profileQueryService.handle(new GetProfileByIdQuery(profileId));
        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get()));
    }

    /**
     * Retrieves a profile by its associated IAM user identifier.
     *
     * @param userId The associated IAM user identifier.
     * @return The {@link ProfileResource} when found, or 404 Not Found.
     */
    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get profile by user ID",
            description = "Retrieves the profile associated with a specific IAM user identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile found",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))
            ),
            @ApiResponse(responseCode = "404", description = "Profile not found for given user")
    })
    public ResponseEntity<ProfileResource> getProfileByUserId(
            @PathVariable
            @Parameter(description = "Associated IAM user identifier", example = "1", required = true)
            Long userId
    ) {
        var profile = profileQueryService.handle(new GetProfileByUserIdQuery(new UserId(userId)));
        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get()));
    }

    /**
     * Updates an existing profile's personal information.
     *
     * @param profileId The unique profile identifier.
     * @param resource  The {@link UpdateProfileResource} request payload.
     * @return The updated {@link ProfileResource} or an error response.
     */
    @PutMapping("/{profileId}")
    @Operation(
            summary = "Update profile",
            description = "Updates the first name, last name, and email address of an existing profile."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Profile not found"),
            @ApiResponse(responseCode = "409", description = "Email address already in use by another profile")
    })
    public ResponseEntity<?> updateProfile(
            @PathVariable
            @Parameter(description = "Unique profile identifier", example = "1", required = true)
            Long profileId,
            @Valid @RequestBody UpdateProfileResource resource
    ) {
        var updateProfileCommand = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(profileId, resource);
        var result = profileCommandService.handle(updateProfileCommand);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                ProfileResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    /**
     * Deletes a profile by its unique identifier.
     *
     * @param profileId The unique profile identifier.
     * @return Confirmation message or error response.
     */
    @DeleteMapping("/{profileId}")
    @Operation(
            summary = "Delete profile",
            description = "Deletes an existing profile by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile deleted successfully",
                    content = @Content(schema = @Schema(implementation = MessageResource.class))
            ),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<?> deleteProfile(
            @PathVariable
            @Parameter(description = "Unique profile identifier", example = "1", required = true)
            Long profileId
    ) {
        var deleteProfileCommand = new DeleteProfileCommand(profileId);
        var result = profileCommandService.handle(deleteProfileCommand)
                .map(_ -> new MessageResource("Profile with given id successfully deleted"));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                message -> message,
                HttpStatus.OK
        );
    }
}
