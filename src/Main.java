import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        String[] texts = new String[25];

        // Создаем список Future
        List<Future> futures = new ArrayList<>();

        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }
        ExecutorService service = Executors.newFixedThreadPool(4);

        long startTs = System.currentTimeMillis(); // start time
        for (String text : texts) {

            // Реализация потока лямбдой интерфейса Callable
            Callable<Integer> task = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            };

            Future<Integer> integerFutureTask = service.submit(task);
            futures.add(integerFutureTask); //Добавляем поток в список потоков
        }

        int max = 0;
        for (Future future : futures) {
            int size = (int) future.get(); // ждём когда поток завершится и вернет значение
            if (max < size) {
                max = size;
            }
        }

        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");

        System.out.println();
        System.out.println("Максимальное значение: " + max);

        service.shutdown();
    }


    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}