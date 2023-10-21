import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class RootFindingMethods extends ApplicationFrame {
    private static final double lowerBound = -2; // Нижня межа
    private static final double upperBound = 2;  // Верхня межа

    // Конструктор для створення вікна з графіком
    public RootFindingMethods(String title, XYSeries series) {
        super(title);
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                title,
                "X",
                "Y",
                createDataset(series),
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(chartPanel);
    }

    private XYSeriesCollection createDataset(XYSeries series) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }

    // Функція
    public static double function(double x) {
        return Math.pow(x, 4) + 4 * x - 2;
    }

    // Похідна від функції
    public static double functionDerivative(double x) {
        return 4 * Math.pow(x, 3) + 4;
    }

    // Метод Ньютона для знаходження кореня
    public static double newtonMethod(double x0, double epsilon) {
        double h = function(x0) / functionDerivative(x0);
        int aprioriEstimate = (int) Math.ceil(Math.log((upperBound - lowerBound) / epsilon) / Math.log(2)); // це припущення, дійсне оцінювання може варіюватися залежно від функції
        System.out.println("Апріорна оцінка кількості ітерацій: " + aprioriEstimate);
        int actualSteps = 0;

        while (Math.abs(h) >= epsilon) {
            h = function(x0) / functionDerivative(x0);
            // Перевірка, чи не стала похідна дуже малою (що може вказувати на проблему)
            if (Math.abs(functionDerivative(x0)) < 1e-6) {
                System.out.println("Похідна навколо нуля в методі Ньютона; корінь може бути неточним.");
                break;
            }
            x0 = x0 - h;
            actualSteps++;
        }

        // Виведення апостеріорної оцінки
        System.out.println("Метод Ньютона виконано за " + actualSteps + " ітерацій.");

        return x0;
    }

    // Модифікований метод Ньютона
    public static double modifiedNewtonMethod(double x0, double epsilon) {
        double derivativeAtInitialPoint = functionDerivative(x0);
        if (Math.abs(derivativeAtInitialPoint) < 1e-6) {
            System.out.println("Похідна навколо початкової точки близька до нуля; модифікований метод може не працювати правильно.");
            return Double.NaN;
        }
        int actualSteps = 0;
        double x1;
        do {
            double functionValue = function(x0);
            x1 = x0 - functionValue / derivativeAtInitialPoint;
            if (Math.abs(x1 - x0) < epsilon) {
                break;
            }
            x0 = x1;
            actualSteps++;
        } while (true);

        System.out.println("Модифікований метод Ньютона виконано за " + actualSteps + " ітерацій.");
        return x1;
    }

    // Метод простих ітерацій
    public static double simpleIterationMethod(double x0, double epsilon) {
        double lambda = 0.01;
        double x1 = x0 - lambda * function(x0);
        int actualSteps = 0;

        while (Math.abs(x1 - x0) > epsilon) {
            x0 = x1;
            x1 = x0 - lambda * function(x0); // Проста ітерація
            actualSteps++;
        }
        System.out.println("Метод простих ітерацій виконано за " + actualSteps + " ітерацій.");
        return x1;
    }

    public static void main(String[] args) {
        int steps = 100;  // Кількість кроків для розділення інтервалу.

        // Створення об'єкту для зберігання значень функції і їх подальшого виводу на графіку.
        XYSeries series = new XYSeries("Функція: f(x) = x^4 + 4x − 2");

        // Обчислення розмірів кроків для поділу інтервалу на steps частин.
        double stepSize = (upperBound - lowerBound) / steps;

        // Цикл для обчислення значень функції на різних точках і зберігання їх у змінну series.
        for (int i = 0; i <= steps; i++) {
            double x = lowerBound + i * stepSize;
            series.add(x, function(x));
        }

        // Створення графіку та відображення його на екрані.
        RootFindingMethods chart = new RootFindingMethods("Графік функції", series);
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);

        double epsilon = 1e-4;  // Точність обчислень.

        double[] initialGuesses = {-1.5, -0.5, 0.5, 1.5};  // Початкові припущення для коренів.

        double maxRoot = -Double.MAX_VALUE;  // Змінна для зберігання максимального кореня.

        // Цикл для обчислення коренів за допомогою трьох різних методів для кожного початкового припущення.
        for (double initialGuess : initialGuesses) {
            System.out.println("Початкова точка: " + initialGuess);

            // Обчислення кореня за допомогою Методу Ньютона та вивід результатів.
            double rootNewton = newtonMethod(initialGuess, epsilon);
            System.out.println("\nМетод Ньютона");
            System.out.println("Корінь: " + rootNewton + ", знайдений на інтервалі: [" + (initialGuess - stepSize/2) + ", " + (initialGuess + stepSize/2) + "]");
            if (rootNewton > maxRoot) {
                maxRoot = rootNewton;
            }

            // Обчислення кореня за допомогою Модифікованого методу Ньютона та вивід результатів.
            double rootModifiedNewton = modifiedNewtonMethod(initialGuess, epsilon);
            System.out.println("\nМодифікований метод Ньютона");
            System.out.println("Корінь: " + rootModifiedNewton + ", знайдений на інтервалі: [" + (initialGuess - stepSize/2) + ", " + (initialGuess + stepSize/2) + "]");
            if (rootModifiedNewton > maxRoot) {
                maxRoot = rootModifiedNewton;
            }

            // Обчислення кореня за допомогою Методу простих ітерацій та вивід результатів.
            double rootSimpleIteration = simpleIterationMethod(initialGuess, epsilon);
            System.out.println("\nМетод простих ітерацій");
            System.out.println("Корінь: " + rootSimpleIteration + ", знайдений на інтервалі: [" + (initialGuess - stepSize/2) + ", " + (initialGuess + stepSize/2) + "]");
            if (rootSimpleIteration > maxRoot) {
                maxRoot = rootSimpleIteration;
            }

            System.out.println();
        }

        // Виведення максимального кореня на екран.
        System.out.println("Найбільший корінь: " + maxRoot);
    }

}
