package axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest.resources;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

import java.util.Date;

/**
 * Request payload for updating an existing incident reported during a tour.
 */
public record UpdateIncidentResource(
        @NotNull(message = "Active tour ID is required")
        @Positive(message = "Active tour ID must be positive")
        Long activeTourId,

        @NotNull(message = "Reported by user ID is required")
        @Positive(message = "Reported by user ID must be positive")
        Long reportedByUserId,

        @NotBlank(message = "Description is required")
        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude must be at least -90.0")
        @DecimalMax(value = "90.0", message = "Latitude must be at most 90.0")
        Double latitude,

        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude must be at least -180.0")
        @DecimalMax(value = "180.0", message = "Longitude must be at most 180.0")
        Double longitude,

        @Nullable Date reportedAt,

        @Nullable String status,

        @Nullable String severity,

        @Nullable
        @Size(max = 1000, message = "Resolution note cannot exceed 1000 characters")
        String resolutionNote,

        @Nullable String reporterRole
) {}