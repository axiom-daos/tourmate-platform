package axiom.daos.tourmate.platform.profiles.infrastructure.persistence.jpa.entities;

import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.EmailAddress;
import axiom.daos.tourmate.platform.profiles.domain.model.valueobjects.UserId;
import axiom.daos.tourmate.platform.profiles.infrastructure.persistence.jpa.converters.EmailAddressPersistenceConverter;
import axiom.daos.tourmate.platform.profiles.infrastructure.persistence.jpa.converters.UserIdPersistenceConverter;
import axiom.daos.tourmate.platform.profiles.infrastructure.persistence.jpa.embeddables.PersonNamePersistenceEmbeddable;
import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity for {@code Profile} aggregates.
 */
@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
public class ProfilePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Embedded
    private PersonNamePersistenceEmbeddable name;

    @Convert(converter = EmailAddressPersistenceConverter.class)
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private EmailAddress email;

    @Convert(converter = UserIdPersistenceConverter.class)
    @Column(name = "user_id", nullable = false, unique = true)
    private UserId userId;
}
