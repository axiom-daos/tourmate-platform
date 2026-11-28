package axiom.daos.tourmate.platform.profiles.domain.model.queries;

import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.EmailAddress;

/**
 * Query to retrieve a profile by email address.
 *
 * @param email The email address value object.
 */
public record GetProfileByEmailQuery(EmailAddress email) {
    public GetProfileByEmailQuery {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
    }
}
