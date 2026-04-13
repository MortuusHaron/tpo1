public class Location {
    private String name;
    private LocationType type;

    public enum LocationType {
        SPACESHIP_BRIDGE,
        SPACESHIP_CABIN,
        SPACE,
        PLANET,
        NEBULA
    }

    public Location(String name, LocationType type) {
        this.name = name;
        this.type = type;
    }

    public boolean isInSpace() {
        return type == LocationType.SPACE || type == LocationType.NEBULA;
    }

    public String getName() { return name; }
    public LocationType getType() { return type; }
}