package axiom.daos.tourmate.platform.tour_management.infrastructure.persistence.jpa.embeddables;

import axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.embeddables.MoneyPersistenceEmbeddable;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.DifficultyLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class TourDetailsPersistenceEmbeddable {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "duration", nullable = false)
    private String duration;

    @Column(name = "diffyculty_level", nullable = false)
    private String difficultyLevel;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "price_amount", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "price_currency", nullable = false))
    })
    private MoneyPersistenceEmbeddable price;

    public TourDetailsPersistenceEmbeddable(String title, String description, String duration, String difficultyLevel, MoneyPersistenceEmbeddable price) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.difficultyLevel = difficultyLevel;
        this.price = price;
    }
}
