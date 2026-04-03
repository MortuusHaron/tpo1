public class Beverage {
    private String name;
    private String effect;
    private int consumptionLevel; // 0-100

    public enum BeverageType {
        PAN_GALACTIC_GARGLE_BLASTER, TEA, WATER, JUICE
    }

    private BeverageType type;

    public Beverage(String name, BeverageType type, String effect) {
        this.name = name;
        this.type = type;
        this.effect = effect;
        this.consumptionLevel = 0;
    }

    public void consume(int amount) {
        this.consumptionLevel = Math.min(100, consumptionLevel + amount);
    }

    public String getName() { return name; }
    public BeverageType getType() { return type; }
    public String getEffect() { return effect; }
    public int getConsumptionLevel() { return consumptionLevel; }
}