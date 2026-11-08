package axiom.daos.tourmate.platform.shared.domain.model.valueobjects;

public record Coordinates(Double x, Double y) {

    public Coordinates {
        if (x == null) {
            throw new IllegalArgumentException("X required");
        }
        if (y == null) {
            throw new IllegalArgumentException("Y required");
        }
        if (x.isNaN() || x.isInfinite()) {
            throw new IllegalArgumentException("X must be a finite number");
        }
        if (y.isNaN() || y.isInfinite()) {
            throw new IllegalArgumentException("Y must be a finite number");
        }

    }

    public Double distanceTo(Coordinates other) {
        if(other == null) {
            throw new IllegalArgumentException("Other coordinates required");
        }

        return Math.hypot(other.x - this.x, other.y - this.y);
    }
}
