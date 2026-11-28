package axiom.daos.tourmate.platform.profiles.domain.model.aggregates;

import axiom.daos.tourmate.platform.profiles.domain.model.commands.CreateProfileCommand;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.EmailAddress;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.PersonName;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.UserId;
import axiom.daos.tourmate.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.util.Objects;

/**
 * Profile aggregate root.
 *
 * <p>Manages personal profile information (first name, last name, email) and relates
 * the profile to an IAM user strictly by identity ({@link UserId}).</p>
 */
@Getter
public class Profile extends AbstractDomainAggregateRoot<Profile> {

    private Long id;
    private PersonName name;
    private EmailAddress email;
    private UserId userId;

    /**
     * Constructs a new Profile aggregate from domain value objects.
     *
     * @param name   The person's name.
     * @param email  The person's email address.
     * @param userId The associated IAM user identifier.
     */
    public Profile(PersonName name, EmailAddress email, UserId userId) {
        this.name = Objects.requireNonNull(name, "Name is required");
        this.email = Objects.requireNonNull(email, "Email is required");
        this.userId = Objects.requireNonNull(userId, "User id is required");
    }

    /**
     * Constructs a new Profile aggregate from primitive values.
     *
     * @param firstName The person's first name.
     * @param lastName  The person's last name.
     * @param email     The person's email address.
     * @param userId    The associated IAM user identifier.
     */
    public Profile(String firstName, String lastName, String email, Long userId) {
        this(
                new PersonName(firstName, lastName),
                new EmailAddress(email),
                new UserId(userId)
        );
    }

    /**
     * Constructs a new Profile aggregate from a {@link CreateProfileCommand}.
     *
     * @param command The profile creation command.
     */
    public Profile(CreateProfileCommand command) {
        this(
                command.firstName(),
                command.lastName(),
                command.email(),
                command.userId()
        );
    }

    /**
     * Reconstitutes an existing Profile aggregate from persistence state.
     *
     * @param id     The persisted profile identifier.
     * @param name   The persisted person name.
     * @param email  The persisted email address.
     * @param userId The persisted user identifier.
     * @return The reconstituted {@link Profile} instance.
     */
    public static Profile reconstitute(Long id, PersonName name, EmailAddress email, UserId userId) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("Profile id cannot be null or less than 1");
        }
        var profile = new Profile(name, email, userId);
        profile.id = id;
        return profile;
    }

    /**
     * Updates the profile's first and last name.
     *
     * @param firstName The new first name.
     * @param lastName  The new last name.
     * @return This updated profile instance.
     */
    public Profile updateName(String firstName, String lastName) {
        this.name = new PersonName(firstName, lastName);
        return this;
    }

    /**
     * Updates the profile's email address.
     *
     * @param email The new email address.
     * @return This updated profile instance.
     */
    public Profile updateEmail(String email) {
        this.email = new EmailAddress(email);
        return this;
    }

    /**
     * Updates the profile's personal information.
     *
     * @param firstName The updated first name.
     * @param lastName  The updated last name.
     * @param email     The updated email address.
     * @return This updated profile instance.
     */
    public Profile updateInformation(String firstName, String lastName, String email) {
        this.name = new PersonName(firstName, lastName);
        this.email = new EmailAddress(email);
        return this;
    }

    /**
     * Returns the first name as a plain string.
     *
     * @return first name
     */
    public String getFirstName() {
        return this.name.firstName();
    }

    /**
     * Returns the last name as a plain string.
     *
     * @return last name
     */
    public String getLastName() {
        return this.name.lastName();
    }

    /**
     * Returns the formatted full name.
     *
     * @return full name
     */
    public String getFullName() {
        return this.name.getFullName();
    }

    /**
     * Returns the email address as a plain string.
     *
     * @return email address
     */
    public String getEmailAddress() {
        return this.email.address();
    }
}
