package axiom.daos.tourmate.platform.shared.domain.model.valueobjects;

import java.math.BigDecimal;

public record Money(BigDecimal amount, Currency currency) {

    public Money {
        if(currency == null) {
            throw new IllegalArgumentException("Currency required");
        }

        if(amount == null) {
            throw new IllegalArgumentException("Amount required");
        }

        if(amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }
    }

    public Money add(Money money) {

        if(money == null) {
            throw new IllegalArgumentException("Money required");
        }
        if(!this.currency.equals(money.currency)) {
            throw new IllegalArgumentException("Currencies are not equals");
        }
        return new Money(this.amount.add(money.amount), this.currency);
    }
}
