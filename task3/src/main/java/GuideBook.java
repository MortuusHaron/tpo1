import java.util.HashMap;
import java.util.Map;

public class GuideBook {
    private String title;
    private Map<String, String> entries;
    private Character owner;
    private boolean isOpen;

    public GuideBook(String title) {
        this.title = title;
        this.entries = new HashMap<>();
        this.isOpen = false;
    }

    public void addEntry(String topic, String content) {
        entries.put(topic, content);
    }

    public String readEntry(String topic) {
        return entries.getOrDefault(topic, "Entry not found");
    }

    public void setOwner(Character character) {
        this.owner = character;
    }

    public void open() {
        this.isOpen = true;
    }

    public void close() {
        this.isOpen = false;
    }

    public String getTitle() { return title; }
    public Character getOwner() { return owner; }
    public boolean isOpen() { return isOpen; }
}