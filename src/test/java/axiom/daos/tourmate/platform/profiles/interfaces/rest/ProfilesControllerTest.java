package axiom.daos.tourmate.platform.profiles.interfaces.rest;

import axiom.daos.tourmate.platform.profiles.application.commandservices.ProfileCommandService;
import axiom.daos.tourmate.platform.profiles.application.queryservices.ProfileQueryService;
import axiom.daos.tourmate.platform.profiles.domain.model.aggregates.Profile;
import axiom.daos.tourmate.platform.profiles.domain.model.commands.CreateProfileCommand;
import axiom.daos.tourmate.platform.profiles.domain.model.commands.DeleteProfileCommand;
import axiom.daos.tourmate.platform.profiles.domain.model.commands.UpdateProfileCommand;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetAllProfilesQuery;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetProfileByIdQuery;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.EmailAddress;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.PersonName;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.UserId;
import axiom.daos.tourmate.platform.profiles.interfaces.rest.resources.CreateProfileResource;
import axiom.daos.tourmate.platform.profiles.interfaces.rest.resources.ProfileResource;
import axiom.daos.tourmate.platform.profiles.interfaces.rest.resources.UpdateProfileResource;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.shared.interfaces.rest.resources.MessageResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfilesControllerTest {

    @Mock
    private ProfileCommandService commandService;

    @Mock
    private ProfileQueryService queryService;

    private ProfilesController controller;

    @BeforeEach
    void setUp() {
        controller = new ProfilesController(commandService, queryService);
    }

    private Profile sampleProfile() {
        return Profile.reconstitute(
                1L,
                new PersonName("John", "Doe"),
                new EmailAddress("john.doe@example.com"),
                new UserId(7L)
        );
    }

    @Test
    void shouldCreateProfileAndReturn201() {
        when(commandService.handle(any(CreateProfileCommand.class)))
                .thenReturn(Result.success(sampleProfile()));

        var response = controller.createProfile(
                new CreateProfileResource("John", "Doe", "john.doe@example.com", 7L)
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        var body = assertInstanceOf(ProfileResource.class, response.getBody());
        assertEquals(1L, body.id());
        assertEquals("John", body.firstName());
        assertEquals("Doe", body.lastName());
        assertEquals("john.doe@example.com", body.email());
        assertEquals(7L, body.userId());
    }

    @Test
    void shouldReturn409ConflictWhenProfileAlreadyExists() {
        when(commandService.handle(any(CreateProfileCommand.class)))
                .thenReturn(Result.failure(ApplicationError.conflict("Profile", "Email already exists")));

        var response = controller.createProfile(
                new CreateProfileResource("John", "Doe", "john.doe@example.com", 7L)
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertInstanceOf(ProblemDetail.class, response.getBody());
    }

    @Test
    void shouldListAllProfiles() {
        when(queryService.handle(any(GetAllProfilesQuery.class)))
                .thenReturn(List.of(sampleProfile()));

        var response = controller.getAllProfiles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().getFirst().id());
    }

    @Test
    void shouldGetProfileByIdAndReturn404WhenMissing() {
        when(queryService.handle(new GetProfileByIdQuery(1L)))
                .thenReturn(Optional.of(sampleProfile()));
        when(queryService.handle(new GetProfileByIdQuery(99L)))
                .thenReturn(Optional.empty());

        var found = controller.getProfileById(1L);
        var notFound = controller.getProfileById(99L);

        assertEquals(HttpStatus.OK, found.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, notFound.getStatusCode());
    }

    @Test
    void shouldGetProfileByUserId() {
        when(queryService.handle(new GetProfileByUserIdQuery(new UserId(7L))))
                .thenReturn(Optional.of(sampleProfile()));

        var response = controller.getProfileByUserId(7L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(7L, response.getBody().userId());
    }

    @Test
    void shouldUpdateProfile() {
        var updated = sampleProfile().updateInformation("Jane", "Doe", "jane.doe@example.com");
        when(commandService.handle(any(UpdateProfileCommand.class)))
                .thenReturn(Result.success(updated));

        var response = controller.updateProfile(
                1L,
                new UpdateProfileResource("Jane", "Doe", "jane.doe@example.com")
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        var body = assertInstanceOf(ProfileResource.class, response.getBody());
        assertEquals("Jane", body.firstName());
        assertEquals("jane.doe@example.com", body.email());
    }

    @Test
    void shouldDeleteProfile() {
        when(commandService.handle(any(DeleteProfileCommand.class)))
                .thenReturn(Result.success(1L));

        var response = controller.deleteProfile(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(MessageResource.class, response.getBody());
    }
}
