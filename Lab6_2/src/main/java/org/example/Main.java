package org.example;
import  java.util.Scanner;
import java.util.Map;
import java.util.concurrent.*;

public class Main {
    private static class ComputingThread implements Runnable{
        private final CachingPrimeChecker primeChecker;
        private final long number;
        public ComputingThread(CachingPrimeChecker primeChecker, long number) {
            this.primeChecker = primeChecker;
            this.number = number;
        }
        public void run() {
            new Thread(() -> {
                final String currentThreadName = Thread.currentThread().getName();
                System.out.printf("[%s] isPrime(%d) = %b%n", currentThreadName, number, primeChecker.isPrime(number));
            }).start();
        }
    }
    private static class CachingPrimeChecker {
        private final Map<Long, Boolean> cache = new ConcurrentHashMap<>();
        public boolean isPrime( final long x) {
            return cache.computeIfAbsent(x, this::computeIfIsPrime);
        }
        private boolean computeIfIsPrime ( long x) {
            final String currentThreadName = Thread.currentThread().getName();
            System.out.printf("\t[%s ] Running computation for: %d%n", currentThreadName, x);
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(10));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (x < 2) {
                return false;
            }
            for (long i = 2; i * i <= x; i++) {
                if (x % i == 0) {
                    return false;
                }
            }
            return true;
        }
    }
    public static void main(String [] args) throws InterruptedException {
        final CachingPrimeChecker primeChecker = new CachingPrimeChecker();
        final Scanner scanner = new Scanner(System.in);
        Thread thread[] = new Thread[4];
        //final ExecutorService executorService = Executors.newFixedThreadPool(4);
        long numbers[] = new long[4];
        int k = 1;
        while (true) {
            if (k == 1) {
                k = 0;
                System.out.println("Enter 4 numbers: ");
                for (int i = 0; i < 4; i++) {
                    System.out.println("Enter number: ");
                    numbers[i] = scanner.nextLong();
                }
                ExecutorService executorService = Executors.newFixedThreadPool(4);
                for (int i = 0; i < 4; i++) {
                    thread[i] = new Thread(new ComputingThread(primeChecker, numbers[i]));
                    executorService.submit(thread[i]);
                }
                //executorService.shutdown();
                try {
                    if (!executorService.awaitTermination(15, TimeUnit.SECONDS)) {
                        executorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }
                while (!executorService.isTerminated());
                if (executorService.isTerminated()) {
                    k = 1;
                }
                System.out.println("Do you want to continue? (yes/no): ");
                String s = scanner.next();
                if (s.equals("no")) {
                    System.exit(0);
                }
            }
        }
    }
}