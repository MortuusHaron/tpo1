public class Character {
    private String name;
    private Location currentLocation;
    private Activity currentActivity;
    private Beverage consuming;
    private GuideBook reading;

    public enum Activity {
        FLYING, DISCUSSING, RELAXING, READING, DRINKING
    }

    public Character(String name) {
        this.name = name;
        this.currentActivity = Activity.RELAXING;
    }

    public void setLocation(Location location) {
        this.currentLocation = location;
    }

    public void setActivity(Activity activity) {
        this.currentActivity = activity;

        // автоматически открываем/закрываем книгу при смене активности
        if (reading != null) {
            if (activity == Activity.READING) {
                reading.open();
            } else {
                reading.close();
            }
        }
    }

    public void startDrinking(Beverage beverage) {
        this.consuming = beverage;
        this.currentActivity = Activity.DRINKING;
    }

    public void startReading(GuideBook book) {
        this.reading = book;
        this.currentActivity = Activity.READING;
        book.open();
    }

    public String getName() { return name; }
    public Location getCurrentLocation() { return currentLocation; }
    public Activity getCurrentActivity() { return currentActivity; }
    public Beverage getConsuming() { return consuming; }
    public GuideBook getReading() { return reading; }
}