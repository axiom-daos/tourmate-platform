package axiom.daos.tourmate.platform.profiles.domain.model.valueobjects;

import org.jspecify.annotations.NullMarked;

import java.util.regex.Pattern;

/**
 * Value object representing a validated email address.
 *
 * @param address The email address string. Cannot be null, blank, or malformed.
 */
@NullMarked
public record EmailAddress(String address) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public EmailAddress {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Email address cannot be null or blank");
        }
        address = address.trim();
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("Email address must be valid");
        }
    }
}
