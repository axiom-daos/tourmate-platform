package axiom.daos.tourmate.platform.shared.domain.model.valueobjects;

public record Currency(String code) {

    public Currency {
        if(code == null || code.isBlank()) {
            throw new IllegalArgumentException("Currency code cannot be null or blank");
        }
        code = code.trim().toUpperCase();
        if(!code.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Currency code must be 3 letter (ISO 4217), got: " + code);
        }
    }
}
