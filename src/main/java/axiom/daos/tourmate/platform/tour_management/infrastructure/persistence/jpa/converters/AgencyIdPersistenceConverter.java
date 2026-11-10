package axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.converters;

import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.AgencyId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class AgencyIdPersistenceConverter implements AttributeConverter<AgencyId, Long> {

    @Override
    public Long convertToDatabaseColumn(AgencyId attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public AgencyId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new AgencyId(dbData);
    }
}
