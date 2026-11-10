package axiom.daos.tourmate.platform.tourmonitoring.infrastructure.persistence.jpa.converters;


import axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects.TourGuideId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts tourGuide IDs between the domain model and persistence column values.
 */
@Converter(autoApply = false)
public class TourGuideIdPersistenceConverter implements AttributeConverter<TourGuideId, Long> {

    @Override
    public Long convertToDatabaseColumn(TourGuideId attribute) {
        return attribute == null ? null : attribute.tourGuideId();
    }

    @Override
    public TourGuideId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new TourGuideId(dbData);
    }
}

