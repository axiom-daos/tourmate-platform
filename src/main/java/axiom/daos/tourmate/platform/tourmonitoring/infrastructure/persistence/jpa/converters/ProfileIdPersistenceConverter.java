package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.converters;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ProfileId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ProfileIdPersistenceConverter implements AttributeConverter<ProfileId, Long> {
    @Override
    public Long convertToDatabaseColumn(ProfileId attribute) {
        return attribute == null ? null : attribute.profileId();
    }

    @Override
    public ProfileId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new ProfileId(dbData);
    }
}
