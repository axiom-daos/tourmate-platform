package axiom.daos.tourmate.platform.tour_management.domain.model.entities;

import axiom.daos.tourmate.platform.shared.domain.model.valueobjects.Coordinates;
import lombok.Getter;

@Getter
public class Checkpoint {

    private Long id;
    private String name;
    private Coordinates location;
    private int order;
    private String description;

    public Checkpoint(String name, Coordinates location, int order, String description) {
        changeName(name);
        changeLocation(location);
        changeOrder(order);
        changeDescription(description);
    }

    public void changeName(String newName) {

        if(newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("New name cannot be null or blank");
        }

        this.name = newName.trim();
    }

    public void changeLocation(Coordinates newLocation) {
        if(newLocation == null) {
            throw new IllegalArgumentException("New location cannot be null");
        }

        this.location = newLocation;
    }

    public void changeOrder(int newOrder) {

        if(newOrder < 0) {
            throw new IllegalArgumentException("New order cannot be negative");
        }

        this.order = newOrder;
    }

    public void changeDescription(String newDescription) {

        if(newDescription == null || newDescription.isBlank()) {
            throw new IllegalArgumentException("New Description cannot be null or blank");
        }

        this.description = newDescription.trim();
    }

    public static Checkpoint reconstitute(Long id, String name, Coordinates location, int order, String description) {
        var checkpoint = new Checkpoint(name, location, order, description);
        checkpoint.id = id;

        return checkpoint;
    }


}
