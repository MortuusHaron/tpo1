import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование B+ дерева (max degree = 7)")
class BPlusTreeTest {

    private BPlusTree tree;

    @BeforeEach
    void setUp() {
        tree = new BPlusTree(7);
    }

    @Test
    @DisplayName("Тест вставки в пустое дерево")
    void testInsertIntoEmpty() {
        tree.insert(10);

        List<String> expectedPath = Arrays.asList(
                "START_INSERT",
                "ROOT_NOT_FULL",
                "INSERT_NON_FULL",
                "LEAF_INSERT",
                "INSERT_COMPLETE"
        );

        assertEquals(expectedPath, tree.getExecutionPath());

        // Проверка что ключ действительно вставлен
        assertTrue(tree.search(10));
    }

    @Test
    @DisplayName("Тест вставки с разделением корня")
    void testInsertWithRootSplit() {
        // put ключи до заполнения корня
        for (int i = 1; i <= 6; i++) {
            tree.insert(i * 10);
        }

        // cлед вставка должна вызвать разделение
        tree.insert(70);

        List<String> expectedPath = Arrays.asList(
                "START_INSERT",
                "ROOT_SPLIT_NEEDED",
                "INSERT_NON_FULL",
                "INTERNAL_NODE_TRAVERSE",
                "INSERT_NON_FULL",
                "LEAF_INSERT",
                "INSERT_COMPLETE"
        );

        List<String> actualPath = tree.getExecutionPath();
        // чек только ключевые моменты
        assertTrue(actualPath.contains("ROOT_SPLIT_NEEDED"));
        assertTrue(actualPath.contains("LEAF_INSERT"));
    }

    @ParameterizedTest
    @MethodSource("provideSearchScenarios")
    @DisplayName("Тест поиска ключей")
    void testSearch(boolean shouldFind, int key, int[] keysToInsert) {
        for (int k : keysToInsert) {
            tree.insert(k);
        }

        boolean found = tree.search(key);
        assertEquals(shouldFind, found);

        List<String> path = tree.getExecutionPath();
        if (shouldFind) {
            assertTrue(path.contains("KEY_FOUND"));
        } else {
            assertTrue(path.contains("LEAF_REACHED_KEY_NOT_FOUND") ||
                    path.contains("KEY_FOUND"));
        }
    }

    @Test
    @DisplayName("Тест удаления из листа")
    void testDeleteFromLeaf() {
        tree.insert(10);
        tree.insert(20);

        tree.delete(10);

        List<String> expectedPath = Arrays.asList(
                "START_DELETE",
                "DELETE_NODE",
                "DELETE_FROM_LEAF",
                "DELETE_COMPLETE"
        );

        assertEquals(expectedPath, tree.getExecutionPath());
        assertFalse(tree.search(10));
    }

    @Test
    @DisplayName("Тест печати дерева")
    void testPrint() {
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);

        // падает?
        assertDoesNotThrow(() -> tree.print());
    }

    @Test
    @DisplayName("Тест очистки дерева")
    void testClear() {
        tree.insert(10);
        tree.insert(20);
        assertTrue(tree.search(10));

        tree.clear();
        assertFalse(tree.search(10));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @DisplayName("Тест множественной вставки")
    void testMultipleInserts(int key) {
        tree.insert(key);
        assertTrue(tree.search(key));
    }

    private static Stream<Arguments> provideSearchScenarios() {
        return Stream.of(
                Arguments.of(true, 15, new int[]{10, 15, 20, 25, 30, 35, 40}),
                Arguments.of(false, 17, new int[]{10, 15, 20, 25, 30, 35, 40}),
                Arguments.of(true, 10, new int[]{10}),
                Arguments.of(false, 5, new int[]{10, 20, 30})
        );
    }
}