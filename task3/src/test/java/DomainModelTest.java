import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование доменной модели Галактики")
public class DomainModelTest {

    private GalaxyState galaxy;

    @BeforeEach
    void setUp() {
        galaxy = new GalaxyState();
    }

    @Test
    @DisplayName("Тест 1: Начальное состояние галактики")
    void testInitialGalaxyState() {
        assertEquals(GalaxyState.GalaxyPhase.TRAVELING, galaxy.getCurrentPhase());
        assertEquals(0.0, galaxy.getTimeFromStart());
        assertEquals("Туманность Конской Головы", galaxy.getCurrentNebula());
        assertNotNull(galaxy.getSpacecraft());
        assertEquals("Полет начался", galaxy.getLastEvent());
    }

    @Test
    @DisplayName("Тест 2: Наличие всех персонажей")
    void testAllCharacters() {
        assertEquals(4, galaxy.getCharacters().size());

        assertTrue(hasCharacter("Зафод"));
        assertTrue(hasCharacter("Форд"));
        assertTrue(hasCharacter("Триллиан"));
        assertTrue(hasCharacter("Артур"));
    }

    @Test
    @DisplayName("Тест 3: Проверка инвариантов в начальном состоянии")
    void testInitialInvariants() {
        assertTrue(galaxy.checkInvariants());
    }

    @Test
    @DisplayName("Тест 4: Переход между фазами полета")
    void testGalaxyPhaseTransitions() {
        assertEquals(GalaxyState.GalaxyPhase.TRAVELING, galaxy.getCurrentPhase());

        galaxy.advanceTime(25);  // >24 -> APPROACHING_NEBULA
        assertEquals(GalaxyState.GalaxyPhase.APPROACHING_NEBULA, galaxy.getCurrentPhase());
        assertTrue(galaxy.getLastEvent().contains("приближается"));

        galaxy.advanceTime(24);  // >48 -> RESTING
        assertEquals(GalaxyState.GalaxyPhase.RESTING, galaxy.getCurrentPhase());
        assertTrue(galaxy.getLastEvent().contains("отдыхает"));

        galaxy.advanceTime(24);  // >72 -> EXPLORING
        assertEquals(GalaxyState.GalaxyPhase.EXPLORING, galaxy.getCurrentPhase());
        assertTrue(galaxy.getLastEvent().contains("исследование"));
    }

    @Test
    @DisplayName("Тест 5: Активности персонажей в фазе RESTING")
    void testActivitiesInRestingPhase() {
        // Переходим в фазу RESTING
        galaxy.advanceTime(49);

        galaxy.performAction("Зафод", "drink");
        galaxy.performAction("Форд", "discuss");
        galaxy.performAction("Триллиан", "discuss");
        galaxy.performAction("Артур", "read");

        Character zafor = findCharacter("Зафод");
        Character ford = findCharacter("Форд");
        Character trillian = findCharacter("Триллиан");
        Character arthur = findCharacter("Артур");

        assertEquals(Character.Activity.DRINKING, zafor.getCurrentActivity(),
                "Зафод должен пить");
        assertEquals(Character.Activity.DISCUSSING, ford.getCurrentActivity(),
                "Форд должен обсуждать");
        assertEquals(Character.Activity.DISCUSSING, trillian.getCurrentActivity(),
                "Триллиан должна обсуждать");
        assertEquals(Character.Activity.READING, arthur.getCurrentActivity(),
                "Артур должен читать");

        // у Зафода есть напиток?
        assertNotNull(zafor.getConsuming());
        assertEquals("Пангалактический бульк-бластер", zafor.getConsuming().getName());

        // у Артура есть книга?
        assertNotNull(arthur.getReading());
    }

    @Test
    @DisplayName("Тест 6: Выполнение действий персонажами")
    void testCharacterActions() {
        galaxy.performAction("Зафод", "fly");
        assertEquals(Character.Activity.FLYING, findCharacter("Зафод").getCurrentActivity());
        assertTrue(galaxy.getLastEvent().contains("управляет"));

        galaxy.performAction("Артур", "read");
        assertEquals(Character.Activity.READING, findCharacter("Артур").getCurrentActivity());
        assertTrue(galaxy.getLastEvent().contains("читает"));
    }

    @Test
    @DisplayName("Тест 7: Негативный сценарий - несуществующий персонаж")
    void testNonExistentCharacter() {
        galaxy.performAction("Незнакомец", "drink");
        assertTrue(galaxy.getLastEvent().contains("не найден"));
    }

    @Test
    @DisplayName("Тест 8: Негативный сценарий - отрицательное время")
    void testNegativeTime() {
        assertThrows(IllegalArgumentException.class, () -> {
            galaxy.advanceTime(-10);
        });
    }

    @Test
    @DisplayName("Тест 9: Проверка напитка и его употребления")
    void testBeverageConsumption() {
        Character zafor = findCharacter("Зафод");
        Beverage drink = zafor.getConsuming();

        assertNotNull(drink);
        assertEquals(0, drink.getConsumptionLevel());

        drink.consume(30);
        assertEquals(30, drink.getConsumptionLevel());

        drink.consume(80); // должно ограничиться 100
        assertEquals(100, drink.getConsumptionLevel());
    }

    @Test
    @DisplayName("Тест 10: Проверка путеводителя")
    void testGuideBook() {
        Character arthur = findCharacter("Артур");
        GuideBook book = arthur.getReading();

        assertNotNull(book, "У Артура должен быть путеводитель");

        // В начальном состоянии Артур читает, поэтому книга должна быть открыта
        // активность READING и открыть книгу
        arthur.setActivity(Character.Activity.READING);
        book.open();
        assertTrue(book.isOpen(), "Книга должна быть открыта при чтении");

        // читает существующую?
        String content = book.readEntry("Туманность Конской Головы");
        assertNotEquals("Entry not found", content, "Должна найтись существующая статья");

        // читает несуществующую?
        assertEquals("Entry not found", book.readEntry("Неизвестно"),
                "Для несуществующей статьи должен вернуться специальный текст");

        book.close();
        assertFalse(book.isOpen(), "Книга должна закрываться");

        // chane активность - книга должна закрыться автоматически
        arthur.setActivity(Character.Activity.RELAXING);
        assertFalse(book.isOpen(), "При смене активности книга должна закрываться");
    }

    @Test
    @DisplayName("Тест 11: Проверка локаций")
    void testLocations() {
        Spacecraft ship = galaxy.getSpacecraft();

        Location bridge = ship.getLocation("мостик");
        assertEquals("мостик", bridge.getName());
        assertEquals(Location.LocationType.SPACESHIP_BRIDGE, bridge.getType());

        Location nebula = new Location("Туманность", Location.LocationType.NEBULA);
        assertTrue(nebula.isInSpace());
    }

    @Test
    @DisplayName("Тест 12: Проверка корабля")
    void testSpacecraft() {
        Spacecraft ship = galaxy.getSpacecraft();

        assertEquals("Золотое Сердце", ship.getName());
        assertTrue(ship.isFlying());
        assertEquals(1000.0, ship.getSpeed());

        ship.stop();
        assertFalse(ship.isFlying());

        ship.fly();
        assertTrue(ship.isFlying());
    }

    @Test
    @DisplayName("Тест 13: Проверка инвариантов после действий")
    void testInvariantsAfterActions() {
        assertTrue(galaxy.checkInvariants());

        galaxy.advanceTime(49);

        galaxy.performAction("Зафод", "drink");
        galaxy.performAction("Форд", "discuss");
        galaxy.performAction("Триллиан", "discuss");
        galaxy.performAction("Артур", "read");

        assertTrue(galaxy.checkInvariants());

        galaxy.performAction("Артур", "relax");
        assertTrue(galaxy.checkInvariants());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Зафод", "Форд", "Триллиан", "Артур"})
    @DisplayName("Тест 14: Поиск каждого персонажа")
    void testFindEachCharacter(String name) {
        assertNotNull(findCharacter(name));
    }

    @ParameterizedTest
    @MethodSource("provideActionTestData")
    @DisplayName("Тест 15: Все возможные действия")
    void testAllActions(String character, String action,
                        Character.Activity expectedActivity, String expectedEventPart) {
        galaxy.performAction(character, action);

        Character c = findCharacter(character);
        assertEquals(expectedActivity, c.getCurrentActivity());
        assertTrue(galaxy.getLastEvent().contains(expectedEventPart));
    }

    private static Stream<Arguments> provideActionTestData() {
        return Stream.of(
                Arguments.of("Зафод", "drink", Character.Activity.DRINKING, "пьет"),
                Arguments.of("Форд", "discuss", Character.Activity.DISCUSSING, "обсуждает"),
                Arguments.of("Триллиан", "discuss", Character.Activity.DISCUSSING, "обсуждает"),
                Arguments.of("Артур", "read", Character.Activity.READING, "читает"),
                Arguments.of("Зафод", "fly", Character.Activity.FLYING, "управляет"),
                Arguments.of("Артур", "relax", Character.Activity.RELAXING, "отдыхает")
        );
    }

    private boolean hasCharacter(String name) {
        return galaxy.getCharacters().stream()
                .anyMatch(c -> c.getName().equals(name));
    }

    private Character findCharacter(String name) {
        return galaxy.getCharacters().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}