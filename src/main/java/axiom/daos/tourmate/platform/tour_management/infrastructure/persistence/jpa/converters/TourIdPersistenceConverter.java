package axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.converters;

import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.TourId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TourIdPersistenceConverter implements AttributeConverter<TourId, Long> {

    @Override
    public Long convertToDatabaseColumn(TourId attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public TourId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new TourId(dbData);
    }
}
