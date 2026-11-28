package axiom.daos.tourmate.platform.tourmonitoring.domain.model.valueobjects;

/**
 * Value object representing geographic coordinates (latitude and longitude).
 *
 * @param latitude  Latitude in decimal degrees [-90, 90].
 * @param longitude Longitude in decimal degrees [-180, 180].
 */
public record GeoLocation(double latitude, double longitude) {

    public GeoLocation {
        if (Double.isNaN(latitude) || Double.isInfinite(latitude) || latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("Latitude must be a finite number between -90 and 90");
        }
        if (Double.isNaN(longitude) || Double.isInfinite(longitude) || longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Longitude must be a finite number between -180 and 180");
        }
    }
}
