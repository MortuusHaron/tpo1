import java.util.HashMap;
import java.util.Map;

public class Spacecraft {
    private String name;
    private Map<String, Location> locations;
    private double speed;
    private boolean isFlying;

    public Spacecraft(String name) {
        this.name = name;
        this.locations = new HashMap<>();
        this.isFlying = true;
        this.speed = 1000.0;
    }

    public void addLocation(String name, Location.LocationType type) {
        locations.put(name, new Location(name, type));
    }

    public Location getLocation(String name) {
        return locations.get(name);
    }

    public void fly() {
        this.isFlying = true;
    }

    public void stop() {
        this.isFlying = false;
    }

    public String getName() { return name; }
    public boolean isFlying() { return isFlying; }
    public double getSpeed() { return speed; }
}