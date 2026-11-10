package axiom.daos.tourmate.platform.shared.interfaces.rest.resources;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateMoneyResource(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Currency is required")
        String currency
) {
}
