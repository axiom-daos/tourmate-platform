package axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class CoordinatesPersistenceEmbeddable {

    @Column(name = "x")
    private Double x;

    @Column(name = "y")
    private Double y;

    public CoordinatesPersistenceEmbeddable(Double x, Double y) {
        this.x = x;
        this.y = y;
    }
}