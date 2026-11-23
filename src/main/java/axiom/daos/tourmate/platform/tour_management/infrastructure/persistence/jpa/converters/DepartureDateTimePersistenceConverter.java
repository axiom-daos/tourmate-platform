package axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.converters;

import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.DepartureDateTime;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDateTime;

@Converter(autoApply = false)
public class DepartureDateTimePersistenceConverter implements AttributeConverter<DepartureDateTime, LocalDateTime> {
    @Override
    public LocalDateTime convertToDatabaseColumn(DepartureDateTime attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public DepartureDateTime convertToEntityAttribute(LocalDateTime dbData) {
        return dbData == null ? null : new DepartureDateTime(dbData);
    }
}
