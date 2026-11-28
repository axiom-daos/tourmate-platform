package axiom.daos.tourmate.platform.profiles.application.internal.commandservices;

import axiom.daos.tourmate.platform.profiles.application.internal.outboundservices.acl.ExternalIamService;
import axiom.daos.tourmate.platform.profiles.domain.model.aggregates.Profile;
import axiom.daos.tourmate.platform.profiles.domain.model.commands.CreateProfileCommand;
import axiom.daos.tourmate.platform.profiles.domain.model.commands.DeleteProfileCommand;
import axiom.daos.tourmate.platform.profiles.domain.model.commands.UpdateProfileCommand;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.EmailAddress;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.PersonName;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.UserId;
import axiom.daos.tourmate.platform.profiles.domain.repositories.ProfileRepository;
import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileCommandServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ExternalIamService externalIamService;

    private ProfileCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProfileCommandServiceImpl(profileRepository, externalIamService);
    }

    @Test
    void shouldCreateProfileSuccessfully() {
        var saved = Profile.reconstitute(
                1L,
                new PersonName("John", "Doe"),
                new EmailAddress("john.doe@example.com"),
                new UserId(7L)
        );

        when(externalIamService.existsUserById(7L)).thenReturn(true);
        when(profileRepository.existsByUserId(new UserId(7L))).thenReturn(false);
        when(profileRepository.existsByEmail(new EmailAddress("john.doe@example.com"))).thenReturn(false);
        when(profileRepository.save(any(Profile.class))).thenReturn(saved);

        var result = service.handle(new CreateProfileCommand("John", "Doe", "john.doe@example.com", 7L));

        assertTrue(result.isSuccess());
        var created = result.toOptional().orElseThrow();
        assertEquals(1L, created.getId());
        assertEquals("John", created.getFirstName());
        assertEquals("Doe", created.getLastName());
        assertEquals("john.doe@example.com", created.getEmailAddress());
        assertEquals(7L, created.getUserId().userId());
    }

    @Test
    void shouldReturnUserNotFoundWhenIamUserDoesNotExist() {
        when(externalIamService.existsUserById(99L)).thenReturn(false);

        var result = service.handle(new CreateProfileCommand("John", "Doe", "john.doe@example.com", 99L));

        assertEquals("USER_NOT_FOUND", getError(result).code());
        verify(profileRepository, never()).save(any());
    }

    @Test
    void shouldRejectDuplicateProfileForSameUser() {
        when(externalIamService.existsUserById(7L)).thenReturn(true);
        when(profileRepository.existsByUserId(new UserId(7L))).thenReturn(true);

        var result = service.handle(new CreateProfileCommand("John", "Doe", "john.doe@example.com", 7L));

        assertEquals("PROFILE_CONFLICT", getError(result).code());
        verify(profileRepository, never()).save(any());
    }

    @Test
    void shouldRejectDuplicateEmailOnCreate() {
        when(externalIamService.existsUserById(7L)).thenReturn(true);
        when(profileRepository.existsByUserId(new UserId(7L))).thenReturn(false);
        when(profileRepository.existsByEmail(new EmailAddress("john.doe@example.com"))).thenReturn(true);

        var result = service.handle(new CreateProfileCommand("John", "Doe", "john.doe@example.com", 7L));

        assertEquals("PROFILE_CONFLICT", getError(result).code());
        verify(profileRepository, never()).save(any());
    }

    @Test
    void shouldReturnValidationErrorWhenEmailFormatIsInvalid() {
        var result = service.handle(new CreateProfileCommand("John", "Doe", "invalid-email", 7L));

        assertEquals("VALIDATION_ERROR", getError(result).code());
        verifyNoInteractions(externalIamService, profileRepository);
    }

    @Test
    void shouldUpdateProfileSuccessfully() {
        var existing = Profile.reconstitute(
                1L,
                new PersonName("John", "Doe"),
                new EmailAddress("john.doe@example.com"),
                new UserId(7L)
        );

        when(profileRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(profileRepository.existsByEmailAndIdIsNot(new EmailAddress("johnny.doe@example.com"), 1L)).thenReturn(false);
        when(profileRepository.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = service.handle(new UpdateProfileCommand(1L, "Johnny", "Doe", "johnny.doe@example.com"));

        assertTrue(result.isSuccess());
        var updated = result.toOptional().orElseThrow();
        assertEquals("Johnny", updated.getFirstName());
        assertEquals("johnny.doe@example.com", updated.getEmailAddress());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingProfile() {
        when(profileRepository.findById(404L)).thenReturn(Optional.empty());

        var result = service.handle(new UpdateProfileCommand(404L, "Johnny", "Doe", "johnny.doe@example.com"));

        assertEquals("PROFILE_NOT_FOUND", getError(result).code());
    }

    @Test
    void shouldDeleteProfileSuccessfully() {
        when(profileRepository.existsById(1L)).thenReturn(true);

        var result = service.handle(new DeleteProfileCommand(1L));

        assertTrue(result.isSuccess());
        assertEquals(1L, result.toOptional().orElseThrow());
        verify(profileRepository).deleteById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingProfile() {
        when(profileRepository.existsById(404L)).thenReturn(false);

        var result = service.handle(new DeleteProfileCommand(404L));

        assertEquals("PROFILE_NOT_FOUND", getError(result).code());
        verify(profileRepository, never()).deleteById(any());
    }

    private static <T> ApplicationError getError(Result<T, ApplicationError> result) {
        assertInstanceOf(Result.Failure.class, result);
        return ((Result.Failure<T, ApplicationError>) result).error();
    }
}
