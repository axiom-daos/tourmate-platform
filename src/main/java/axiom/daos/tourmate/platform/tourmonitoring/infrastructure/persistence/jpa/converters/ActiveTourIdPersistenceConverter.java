package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.converters;

import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.ActiveTourId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ActiveTourIdPersistenceConverter implements AttributeConverter<ActiveTourId, Long> {

    @Override
    public Long convertToDatabaseColumn(ActiveTourId attribute) {
        return attribute == null ? null : attribute.activeTourId();
    }

    @Override
    public ActiveTourId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new ActiveTourId(dbData);
    }
}
