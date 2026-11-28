package axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest.resources;

import java.util.Date;

/**
 * Represents the resource for an incident reported during a tour. This resource captures the details of the incident, including its unique identifier, associated tour, reporter information, description, location, status, severity, resolution notes, and the role of the reporter.
 * @param id
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
public record IncidentResource(
        Long id,
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