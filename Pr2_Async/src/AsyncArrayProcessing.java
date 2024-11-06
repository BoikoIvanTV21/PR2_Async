import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

public class AsyncArrayProcessing {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Запит користувача на введення діапазону
        System.out.print("Введіть мінімальне значення діапазону: ");
        int minRange = scanner.nextInt();

        System.out.print("Введіть максимальне значення діапазону: ");
        int maxRange = scanner.nextInt();

        // Генерація випадкового розміру масиву від 40 до 60
        int arraySize = new Random().nextInt(21) + 40;
        int chunkSize = 10;

        // Створення масиву з випадковими значеннями
        int[] numbers = generateRandomArray(arraySize, minRange, maxRange);
        System.out.println("Згенерований масив: ");
        for (int number : numbers) {
            System.out.print(number + " ");
        }
        System.out.println();

        long startTime = System.currentTimeMillis();  // Початок заміру часу

        // Асинхронна обробка масиву
        double totalSum = processArrayAsync(numbers, chunkSize);
        double average = totalSum / numbers.length;

        long endTime = System.currentTimeMillis();  // Кінець заміру часу
        System.out.println("Загальна сума: " + totalSum);
        System.out.println("Середнє значення масиву: " + average);
        System.out.println("Час виконання програми: " + (endTime - startTime) + " мс");
    }

    // Метод для генерування випадкового масиву
    private static int[] generateRandomArray(int size, int min, int max) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt((max - min) + 1) + min;
        }
        return array;
    }

    // Метод для асинхронної обробки масиву та обчислення загальної суми
    private static double processArrayAsync(int[] array, int chunkSize) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Double>> futures = new ArrayList<>();
        double totalSum = 0;

        // Розбиття масиву на частини та відправка у потоки
        for (int i = 0; i < array.length; i += chunkSize) {
            int start = i;
            int end = Math.min(i + chunkSize, array.length);

            Future<Double> future = executor.submit(() -> {
                double sum = 0;
                for (int j = start; j < end; j++) {
                    sum += array[j];
                }
                return sum;
            });
            futures.add(future);
        }

        // Очікування завершення всіх потоків та збір результатів
        for (Future<Double> future : futures) {
            try {
                totalSum += future.get();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Помилка при отриманні результату: " + e.getMessage());
            }
        }

        // Завершення виконання потоків
        executor.shutdown();

        return totalSum;
    }
}
