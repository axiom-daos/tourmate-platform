package axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest.resources;

import java.util.Date;

public record UpdateIncidentResource(
        String uuid,
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