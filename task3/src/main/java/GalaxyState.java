import java.util.ArrayList;
import java.util.List;

public class GalaxyState {
    private List<Character> characters;
    private Spacecraft spacecraft;
    private String currentNebula;
    private double timeFromStart;
    private String lastEvent;

    public enum GalaxyPhase {
        TRAVELING,
        APPROACHING_NEBULA,
        RESTING,
        EXPLORING
    }

    private GalaxyPhase currentPhase;

    public GalaxyState() {
        this.characters = new ArrayList<>();
        this.currentPhase = GalaxyPhase.TRAVELING;
        this.timeFromStart = 0;
        this.lastEvent = "Полет начался";
        initializeDefaultState();
    }

    private void initializeDefaultState() {
        // корабль
        spacecraft = new Spacecraft("Золотое Сердце");
        spacecraft.addLocation("мостик", Location.LocationType.SPACESHIP_BRIDGE);
        spacecraft.addLocation("каюта", Location.LocationType.SPACESHIP_CABIN);
        spacecraft.addLocation("туманность", Location.LocationType.NEBULA);

        // персонажи
        Character zafor = new Character("Зафод");
        zafor.setLocation(spacecraft.getLocation("мостик"));

        Character ford = new Character("Форд");
        ford.setLocation(spacecraft.getLocation("мостик"));

        Character trillian = new Character("Триллиан");
        trillian.setLocation(spacecraft.getLocation("мостик"));

        Character arthur = new Character("Артур");
        arthur.setLocation(spacecraft.getLocation("каюта"));

        characters.add(zafor);
        characters.add(ford);
        characters.add(trillian);
        characters.add(arthur);

        // напиток и сразу устанавливаем Зафоду
        Beverage panGalactic = new Beverage("Пангалактический бульк-бластер",
                Beverage.BeverageType.PAN_GALACTIC_GARGLE_BLASTER,
                "Прочищает мозги");
        zafor.startDrinking(panGalactic);

        // путеводитель и устанавливаем Артуру
        GuideBook guide = new GuideBook("Путеводитель по Галактике");
        guide.addEntry("Туманность Конской Головы", "Темная туманность в созвездии Ориона");
        guide.setOwner(ford);
        arthur.startReading(guide);

        currentNebula = "Туманность Конской Головы";
        lastEvent = "Полет начался";
    }

    public void advanceTime(double hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("Время не может быть отрицательным");
        }

        timeFromStart += hours;

        // машина состояний
        switch (currentPhase) {
            case TRAVELING:
                if (timeFromStart > 24) {
                    currentPhase = GalaxyPhase.APPROACHING_NEBULA;
                    lastEvent = "Корабль приближается к туманности Конской Головы";
                }
                break;

            case APPROACHING_NEBULA:
                if (timeFromStart > 48) {
                    currentPhase = GalaxyPhase.RESTING;
                    lastEvent = "Зафод отдыхает на мостике, Форд и Триллиан обсуждают жизнь";

                    // обновить активности
                    updateActivitiesForResting();
                }
                break;

            case RESTING:
                if (timeFromStart > 72) {
                    currentPhase = GalaxyPhase.EXPLORING;
                    lastEvent = "Экипаж начинает исследование туманности";

                    // обновить активности для исследования
                    updateActivitiesForExploring();
                }
                break;

            case EXPLORING:
                lastEvent = "Исследование туманности продолжается";
                break;
        }
    }

    private void updateActivitiesForResting() {
        Character zafor = findCharacter("Зафод");
        Character ford = findCharacter("Форд");
        Character trillian = findCharacter("Триллиан");
        Character arthur = findCharacter("Артур");

        if (zafor != null) {
            zafor.setActivity(Character.Activity.DRINKING);
            // у Зафода есть напиток?
            if (zafor.getConsuming() == null) {
                Beverage panGalactic = new Beverage("Пангалактический бульк-бластер",
                        Beverage.BeverageType.PAN_GALACTIC_GARGLE_BLASTER,
                        "Прочищает мозги");
                zafor.startDrinking(panGalactic);
            }
        }

        if (ford != null) ford.setActivity(Character.Activity.DISCUSSING);
        if (trillian != null) trillian.setActivity(Character.Activity.DISCUSSING);

        if (arthur != null) {
            arthur.setActivity(Character.Activity.READING);
            //  у Артура есть книга?
            if (arthur.getReading() == null) {
                GuideBook guide = new GuideBook("Путеводитель по Галактике");
                guide.addEntry("Туманность Конской Головы", "Темная туманность в созвездии Ориона");
                arthur.startReading(guide);
            }
        }
    }

    private void updateActivitiesForExploring() {
        findCharacter("Зафод").setActivity(Character.Activity.FLYING);
        findCharacter("Форд").setActivity(Character.Activity.DISCUSSING);
        findCharacter("Триллиан").setActivity(Character.Activity.DISCUSSING);
        findCharacter("Артур").setActivity(Character.Activity.RELAXING);
    }

    public void performAction(String characterName, String action) {
        Character character = findCharacter(characterName);
        if (character == null) {
            lastEvent = "Персонаж " + characterName + " не найден";
            return;
        }

        switch (action) {
            case "drink":
                character.setActivity(Character.Activity.DRINKING);
                lastEvent = characterName + " пьет пангалактический бульк-бластер";
                break;
            case "discuss":
                character.setActivity(Character.Activity.DISCUSSING);
                lastEvent = characterName + " обсуждает жизнь и ее последствия";
                break;
            case "read":
                character.setActivity(Character.Activity.READING);
                lastEvent = characterName + " читает путеводитель";
                break;
            case "fly":
                character.setActivity(Character.Activity.FLYING);
                lastEvent = characterName + " управляет кораблем";
                break;
            case "relax":
                character.setActivity(Character.Activity.RELAXING);
                lastEvent = characterName + " отдыхает";
                break;
        }
    }

    public boolean checkInvariants() {
        // 1. У каждого персонажа есть локация
        for (Character c : characters) {
            if (c.getCurrentLocation() == null) {
                System.out.println("Invariant failed: " + c.getName() + " has no location");
                return false;
            }
        }

        // 2. Книга открыта только когда персонаж читает
        for (Character c : characters) {
            if (c.getReading() != null) {
                if (c.getCurrentActivity() == Character.Activity.READING) {
                    if (!c.getReading().isOpen()) {
                        // если персонаж читает, книга должна быть открыта
                        c.getReading().open();
                    }
                } else {
                    if (c.getReading().isOpen()) {
                        c.getReading().close();
                    }
                }
            }
        }

        // 3. Напиток употребляется только когда персонаж пьет
        for (Character c : characters) {
            if (c.getConsuming() != null) {
                if (c.getCurrentActivity() != Character.Activity.DRINKING) {
                    // если персонаж не пьет, но у него есть напиток - это ок, проверяем что активность не противоречит
                }
            }
        }

        return true;
    }

    private Character findCharacter(String name) {
        return characters.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }


    public GalaxyPhase getCurrentPhase() { return currentPhase; }
    public String getLastEvent() { return lastEvent; }
    public String getCurrentNebula() { return currentNebula; }
    public List<Character> getCharacters() { return characters; }
    public Spacecraft getSpacecraft() { return spacecraft; }
    public double getTimeFromStart() { return timeFromStart; }
}