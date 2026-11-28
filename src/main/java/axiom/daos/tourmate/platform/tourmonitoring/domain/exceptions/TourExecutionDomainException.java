package axiom.daos.tourmate.platform.tourmonitoring.domain.exceptions;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import lombok.Getter;

/**
 * Domain exception representing invariant, state machine, or business rule violations
 * within the {@code TourExecution} aggregate root.
 */
@Getter
public class TourExecutionDomainException extends RuntimeException {

    public enum Category {
        CONFLICT,
        BUSINESS_RULE,
        NOT_FOUND,
        VALIDATION,
        FORBIDDEN
    }

    private final String errorCode;
    private final Category category;

    public TourExecutionDomainException(String errorCode, Category category, String message) {
        super(message);
        this.errorCode = errorCode;
        this.category = category;
    }

    public static TourExecutionDomainException conflict(String errorCode, String message) {
        return new TourExecutionDomainException(errorCode, Category.CONFLICT, message);
    }

    public static TourExecutionDomainException businessRule(String errorCode, String message) {
        return new TourExecutionDomainException(errorCode, Category.BUSINESS_RULE, message);
    }

    public static TourExecutionDomainException notFound(String resourceType, String identifier) {
        return new TourExecutionDomainException(
                "%s_NOT_FOUND".formatted(resourceType.toUpperCase()),
                Category.NOT_FOUND,
                "%s not found: %s".formatted(resourceType, identifier)
        );
    }

    public ApplicationError toApplicationError() {
        return switch (category) {
            case CONFLICT -> ApplicationError.stateConflict(errorCode, getMessage(), getMessage());
            case BUSINESS_RULE -> ApplicationError.domainRule(errorCode, getMessage(), getMessage());
            case NOT_FOUND -> new ApplicationError(errorCode, getMessage(), null);
            case FORBIDDEN -> ApplicationError.forbidden(errorCode, getMessage());
            case VALIDATION -> ApplicationError.validationError(errorCode, getMessage());
        };
    }
}
