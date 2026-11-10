package axiom.daos.tourmate.platform.safety_and_incident_management.interfaces.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/locations")
@Tag(name = "Safety Monitoring", description = "Safety Monitoring and Locations CRUD Endpoints")
/**
 * Controller class for managing safety monitoring locations, providing RESTful endpoints for creating, retrieving, updating, and deleting location records. This controller uses an in-memory storage mechanism to manage location data.
 */
public class SafetyMonitoringController {

    private final Map<Long, Map<String, Object>> storage = new HashMap<>();
    private long sequenceId = 1L;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllLocations() {
        return ResponseEntity.ok(storage.values().stream().toList());
    }

    /**
     * Creates a new location record with the provided payload and assigns it a unique ID. The new record is stored in the in-memory storage.
     * @param payload
     * @return
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createLocation(@RequestBody Map<String, Object> payload) {
        long id = sequenceId++;
        Map<String, Object> record = new HashMap<>(payload);
        record.put("id", id);
        storage.put(id, record);
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    /**
     *  Retrieves a location by its ID. If the location is not found, returns a 404 Not Found response.
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getLocationById(@PathVariable Long id) {
        Map<String, Object> record = storage.get(id);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(record);
    }
/**
     * Updates an existing location by its ID. If the location does not exist, returns a 404 Not Found response.
     * @param id The ID of the location to update.
     * @param payload The updated location data.
     * @return A ResponseEntity containing the updated location data or a 404 Not Found response if the location does not exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateLocation(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        if (!storage.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> record = new HashMap<>(payload);
        record.put("id", id);
        storage.put(id, record);
        return ResponseEntity.ok(record);
    }
/**
     * Deletes a location by its ID. If the location does not exist, returns a 404 Not Found response.
     * @param id The ID of the location to delete.
     * @return A ResponseEntity indicating the result of the deletion operation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        if (!storage.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        storage.remove(id);
        return ResponseEntity.noContent().build();
    }
}