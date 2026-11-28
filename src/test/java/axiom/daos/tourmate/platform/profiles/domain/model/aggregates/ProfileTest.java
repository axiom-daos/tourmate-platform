package axiom.daos.tourmate.platform.profiles.domain.model.aggregates;

import axiom.daos.tourmate.platform.profiles.domain.model.commands.CreateProfileCommand;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.EmailAddress;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.PersonName;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.ProfileId;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {

    @Test
    void shouldCreateProfileWithValidData() {
        var command = new CreateProfileCommand("  John ", " Doe  ", " john.doe@example.com ", 5L);
        var profile = new Profile(command);

        assertNull(profile.getId());
        assertEquals("John", profile.getFirstName());
        assertEquals("Doe", profile.getLastName());
        assertEquals("John Doe", profile.getFullName());
        assertEquals("john.doe@example.com", profile.getEmailAddress());
        assertEquals(5L, profile.getUserId().userId());
    }

    @Test
    void shouldRejectInvalidPersonNameEmailOrIds() {
        assertThrows(IllegalArgumentException.class, () -> new PersonName("", "Doe"));
        assertThrows(IllegalArgumentException.class, () -> new PersonName("John", "   "));
        assertThrows(IllegalArgumentException.class, () -> new EmailAddress("not-an-email"));
        assertThrows(IllegalArgumentException.class, () -> new EmailAddress(" "));
        assertThrows(IllegalArgumentException.class, () -> new UserId(0L));
        assertThrows(IllegalArgumentException.class, () -> new ProfileId(0L));
    }

    @Test
    void shouldReconstituteProfileFromPersistence() {
        var profile = Profile.reconstitute(
                10L,
                new PersonName("Jane", "Smith"),
                new EmailAddress("jane.smith@example.com"),
                new UserId(3L)
        );

        assertEquals(10L, profile.getId());
        assertEquals("Jane", profile.getFirstName());
        assertEquals("Smith", profile.getLastName());
        assertEquals("jane.smith@example.com", profile.getEmailAddress());
        assertEquals(3L, profile.getUserId().userId());
    }

    @Test
    void shouldRejectInvalidReconstitutionId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Profile.reconstitute(
                        0L,
                        new PersonName("Jane", "Smith"),
                        new EmailAddress("jane.smith@example.com"),
                        new UserId(3L)
                )
        );
    }

    @Test
    void shouldUpdateProfileInformationWithoutChangingUserId() {
        var profile = Profile.reconstitute(
                10L,
                new PersonName("Jane", "Smith"),
                new EmailAddress("jane.smith@example.com"),
                new UserId(3L)
        );

        var updated = profile.updateInformation("Janet", "Doe", "janet.doe@example.com");

        assertSame(profile, updated);
        assertEquals("Janet", updated.getFirstName());
        assertEquals("Doe", updated.getLastName());
        assertEquals("janet.doe@example.com", updated.getEmailAddress());
        assertEquals(3L, updated.getUserId().userId());
    }
}
