package axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects;

public enum Status {
    IN_PROGRESS,
    FINISHED;

    public static Status fromValue(String status) {
        return switch (status) {
            case "IN_PROGRESS" -> IN_PROGRESS;
            case "FINISHED" -> FINISHED;
            default -> throw new IllegalArgumentException("Invalid status value: " + status);
        };

    }
}
