public class SinClass {
    private static final double PI = 3.14159265358979323846;
    private static final double TWO_PI = 2 * PI;
    /**
     * Вычисление sin(x) разложением в ряд Тейлора
     * sin(x) = x - x^3/3! + x^5/5! - x^7/7! + ...
     */
    public static double sin(double x, double epsilon) {
        // приведение x к диапазону -2pi, 2pi
        x = normalizeAngle(x);

        double result = 0.0;
        double term = x; // первый член ряда
        int n = 1;
        int maxIterations = 1000;

        while (Math.abs(term) > epsilon && n < maxIterations) {
            result += term;
            term = -term * x * x / ((2.0 * n) * (2.0 * n + 1.0));
            n++;
        }

        return result;
    }

    private static double normalizeAngle(double x) {
        x = x % TWO_PI;
        if (x > PI) {
            x -= TWO_PI;
        } else if (x < -PI) {
            x += TWO_PI;
        }
        return x;
    }

    public static double getTableValue(double x) {
        // x к базовому диапазону для сравнения с табличными углами
        double normalized = normalizeAngle(x);

        // c погрешностью double
        if (Math.abs(normalized) < 1e-10) return 0.0;
        if (Math.abs(normalized - PI/6) < 1e-10) return 0.5;
        if (Math.abs(normalized - PI/4) < 1e-10) return 0.7071067811865475; // √2/2
        if (Math.abs(normalized - PI/3) < 1e-10) return 0.8660254037844386; // √3/2
        if (Math.abs(normalized - PI/2) < 1e-10) return 1.0;
        if (Math.abs(normalized - PI) < 1e-10) return 0.0;
        if (Math.abs(normalized - 3*PI/2) < 1e-10) return -1.0;
        if (Math.abs(normalized - TWO_PI) < 1e-10) return 0.0;

        return Double.NaN; // для других значений приближенные вычисления
    }
}