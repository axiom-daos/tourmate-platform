package axiom.daos.tourmate.platform.shared.interfaces.rest.resources;

import java.math.BigDecimal;

public record MoneyResource(
    BigDecimal amount,
    String currency
) {
}
