package axiom.daos.tourmate.platform.profiles.domain.model.valueobjects;

import org.jspecify.annotations.NullMarked;

/**
 * Value object representing a person's first and last name.
 *
 * @param firstName The person's first name. Cannot be null or blank.
 * @param lastName  The person's last name. Cannot be null or blank.
 */
@NullMarked
public record PersonName(String firstName, String lastName) {

    public PersonName {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or blank");
        }
        firstName = firstName.trim();
        lastName = lastName.trim();
    }

    /**
     * Returns the full name combining first name and last name.
     *
     * @return formatted full name
     */
    public String getFullName() {
        return "%s %s".formatted(firstName, lastName);
    }
}
