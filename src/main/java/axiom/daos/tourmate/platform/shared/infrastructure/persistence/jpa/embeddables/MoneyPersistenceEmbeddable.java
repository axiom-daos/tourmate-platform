package axiom.daos.tourmate.platform.shared.infrastructure.persistence.jpa.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class MoneyPersistenceEmbeddable {

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    public MoneyPersistenceEmbeddable(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }
}