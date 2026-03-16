import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Тестирование разложения sin(x) в ряд Тейлора")
class SinClassTest {

    private static final double PI = 3.14159265358979323846;
    private static final double TWO_PI = 2 * PI;
    private static final double TEST_EPSILON = 1e-6;

    @ParameterizedTest
    @MethodSource("provideSpecialAngles")
    @DisplayName("Тест особых углов (табличные значения)")
    void testSpecialAngles(double x, double expected) {
        double result = SinClass.sin(x, 1e-10);
        assertEquals(expected, result, TEST_EPSILON,
                String.format("sin(%f) должно быть %f, получено %f", x, expected, result));
    }

    @ParameterizedTest
    @MethodSource("provideBoundaryAngles")
    @DisplayName("Тест граничных значений")
    void testBoundaryAngles(double x) {
        // табличные значения
        Double tableValue = getTableValueIfExists(x);
        if (tableValue != null) {
            double result = SinClass.sin(x, 1e-10);
            assertEquals(tableValue, result, TEST_EPSILON);
        }
    }

    @ParameterizedTest
    @MethodSource("provideAnglesForNormalization")
    @DisplayName("Тест нормализации углов")
    void testAngleNormalization(double x) {
        // Сравниваем с эталоном через приближение к табличным значениям
        double result = SinClass.sin(x, 1e-10);
        double expected = approximateSin(x);

        assertEquals(expected, result, 1e-4,
                String.format("Ошибка при x=%f", x));
    }

    @ParameterizedTest
    @MethodSource("providePrecisionTestData")
    @DisplayName("Тест точности при разных epsilon")
    void testPrecision(double x, double epsilon) {
        double result = SinClass.sin(x, epsilon);
        double expected = approximateSin(x);
        double error = Math.abs(result - expected);
        assertTrue(error < epsilon * 10,
                String.format("Ошибка %f превышает допустимую %f для x=%f", error, epsilon, x));
    }

    // Аппроксимация sin рядом Тейлора (для тестов, без Math)
    private double approximateSin(double x) {
        // Используем разложение до 10 членов
        x = normalizeAngleForTest(x);
        double result = 0;
        double term = x;
        for (int n = 1; n <= 10; n++) {
            result += term;
            term = -term * x * x / ((2*n) * (2*n + 1));
        }
        return result;
    }

    private double normalizeAngleForTest(double x) {
        x = x % TWO_PI;
        if (x > PI) x -= TWO_PI;
        if (x < -PI) x += TWO_PI;
        return x;
    }

    private Double getTableValueIfExists(double x) {
        double norm = normalizeAngleForTest(x);
        if (Math.abs(norm) < 1e-10) return 0.0;
        if (Math.abs(norm - PI/6) < 1e-10) return 0.5;
        if (Math.abs(norm - PI/4) < 1e-10) return 0.7071067811865475;
        if (Math.abs(norm - PI/3) < 1e-10) return 0.8660254037844386;
        if (Math.abs(norm - PI/2) < 1e-10) return 1.0;
        if (Math.abs(norm - PI) < 1e-10) return 0.0;
        if (Math.abs(norm - 3*PI/2) < 1e-10) return -1.0;
        if (Math.abs(norm - TWO_PI) < 1e-10) return 0.0;
        return null;
    }

    private static Stream<Arguments> provideSpecialAngles() {
        return Stream.of(
                Arguments.of(0.0, 0.0),
                Arguments.of(PI/6, 0.5),
                Arguments.of(PI/4, 0.7071067811865475),
                Arguments.of(PI/3, 0.8660254037844386),
                Arguments.of(PI/2, 1.0),
                Arguments.of(PI, 0.0),
                Arguments.of(3*PI/2, -1.0),
                Arguments.of(TWO_PI, 0.0)
        );
    }

    private static Stream<Arguments> provideBoundaryAngles() {
        return Stream.of(
                Arguments.of(-PI),
                Arguments.of(-PI/2),
                Arguments.of(0.0),
                Arguments.of(PI/2),
                Arguments.of(PI),
                Arguments.of(3*PI/2),
                Arguments.of(TWO_PI)
        );
    }

    private static Stream<Arguments> provideAnglesForNormalization() {
        return Stream.of(
                Arguments.of(3.5),      // > PI
                Arguments.of(-3.5),      // < -PI
                Arguments.of(7.0),       // > TWO_PI
                Arguments.of(-7.0),       // < -TWO_PI
                Arguments.of(100.0)       // многократное превышение
        );
    }

    private static Stream<Arguments> providePrecisionTestData() {
        return Stream.of(
                Arguments.of(1.0, 1e-3),
                Arguments.of(1.0, 1e-5),
                Arguments.of(1.0, 1e-7),
                Arguments.of(PI/4, 1e-4),
                Arguments.of(PI/3, 1e-6),
                Arguments.of(PI/2, 1e-8)
        );
    }
}