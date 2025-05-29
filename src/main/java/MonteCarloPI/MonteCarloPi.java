package MonteCarloPI;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MonteCarloPi {

    public static final double R = 1.0 ;
    public static final int max = 1  ;
    public static final int min= -1 ;
    static final long NUM_POINTS = 50_000_000L;
    static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();




    public static void main(String[] args) throws InterruptedException, ExecutionException
    {
        // Without Threads
        System.out.println("Single threaded calculation started: ");
        long startTime = System.nanoTime();
        double piWithoutThreads = estimatePiWithoutThreads(NUM_POINTS);
        long endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (single thread): " + piWithoutThreads);
        System.out.println("Time taken (single threads): " + (endTime - startTime) / 1_000_000 + " ms");

        // With Threads
        System.out.printf("Multi threaded calculation started: (your device has %d logical threads)\n",NUM_THREADS);
        startTime = System.nanoTime();
        double piWithThreads = estimatePiWithThreads(NUM_POINTS, NUM_THREADS);
        endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (Multi-threaded): " + piWithThreads);
        System.out.println("Time taken (Multi-threaded): " + (endTime - startTime) / 1_000_000 + " ms");


    }



    // Monte Carlo Pi Approximation without threads
    public static double estimatePiWithoutThreads(long numPoints) {

        int inthecricle = 0 ;

        Random rand = new Random();
        for(int i = 0 ; i < numPoints ; i++){

            double x = (-1 + 2 * rand.nextDouble());
            double y = (-1 + 2 * rand.nextDouble());

            if (Math.pow(x,2) + Math.pow(y,2)  <= R){
                inthecricle++;
            }

        }

        double pi = ((double) inthecricle / numPoints) * 4.0;

        return pi;
    }




    // Monte Carlo Pi Approximation with threads
    public static double estimatePiWithThreads(long numPoints, int numThreads) throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        AtomicInteger incircle = new AtomicInteger(0);
        int pointsPerThread = (int) (numPoints / numThreads);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                Random rand = new Random();
                for (int j = 0; j < pointsPerThread; j++) {
                    double x = -1 + 2 * rand.nextDouble();
                    double y = -1 + 2 * rand.nextDouble();
                    if (x * x + y * y <= R) {
                        incircle.getAndIncrement();
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        double pi = ((double) incircle.get() / numPoints) * 4.0;
        return pi;
    }


}