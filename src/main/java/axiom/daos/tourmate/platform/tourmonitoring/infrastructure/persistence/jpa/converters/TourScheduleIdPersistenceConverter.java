package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.converters;


import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourScheduleId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts tourSchedule IDs between the domain model and persistence column values.
 */
@Converter(autoApply = false)
public class TourScheduleIdPersistenceConverter implements AttributeConverter<TourScheduleId, Long> {

    @Override
    public Long convertToDatabaseColumn(TourScheduleId attribute) {
        return attribute == null ? null : attribute.tourScheduleId();
    }

    @Override
    public TourScheduleId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new TourScheduleId(dbData);
    }
}

