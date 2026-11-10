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
public class SafetyMonitoringController {

    // Nota: Para mantener el enfoque estricto en CRUD y la compatibilidad con el mock/store de locations del front,
    // gestionamos los registros de seguridad asociados o un almacenamiento relacional directo.
    private final Map<Long, Map<String, Object>> storage = new HashMap<>();
    private long sequenceId = 1L;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllLocations() {
        return ResponseEntity.ok(storage.values().stream().toList());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createLocation(@RequestBody Map<String, Object> payload) {
        long id = sequenceId++;
        Map<String, Object> record = new HashMap<>(payload);
        record.put("id", id);
        storage.put(id, record);
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getLocationById(@PathVariable Long id) {
        Map<String, Object> record = storage.get(id);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(record);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        if (!storage.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        storage.remove(id);
        return ResponseEntity.noContent().build();
    }
}