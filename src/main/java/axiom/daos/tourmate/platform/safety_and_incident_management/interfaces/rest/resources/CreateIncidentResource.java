package axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest.resources;

import java.util.Date;

/**
 * Represents the resource for creating a new incident reported during a tour. This resource captures the necessary details such as the associated tour, reporter information, incident description, location, status, severity, and any resolution notes.
 * @param activeTourId
 * @param reportedByUserId
 * @param description
 * @param latitude
 * @param longitude
 * @param reportedAt
 * @param status
 * @param severity
 * @param resolutionNote
 * @param reporterRole
 */
public record CreateIncidentResource(
        Long activeTourId,
        Long reportedByUserId,
        String description,
        Double latitude,
        Double longitude,
        Date reportedAt,
        String status,
        String severity,
        String resolutionNote,
        String reporterRole
) {}