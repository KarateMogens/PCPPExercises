package exercises07;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import static org.junit.jupiter.api.Assertions.*;

// Data structures imports
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;

// Concurrency imports
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

public class TestHistograms {
    // The imports above are just for convenience, feel free add or remove imports
    
    // TODO: 10.1.2
    private CasHistogram threadSafeHistogram;
    private Histogram1 sequentialHistogram;
    private final int range = 4_999_999;
    private final int span = 25;
    private int[] sequentialResults;
    private int[] concurrentResults;

    @BeforeEach
    public void initializeTest() {
        threadSafeHistogram = new CasHistogram(25);
        sequentialHistogram = new Histogram1(25);
        sequentialResults = new int[span];
        concurrentResults = new int[span];
    }


    @RepeatedTest(100)
    @ParameterizedTest
    @ValueSource(ints = {0,1,2,3,4})
    public void testCasHistogramEqualsHistogram1CountPrimesTask(int threads) {

        int threadCount = (int) Math.pow(2, threads);

        CyclicBarrier myCyclicBarrier = new CyclicBarrier(threadCount);

        //Sequential Histogram
        // Create and get results from sequential histogram
        for (int i = 0; i < range; i++) {
            int binToUpdate = countFactors(i);
            sequentialHistogram.increment(binToUpdate);
        }
       
        for (int i = 0; i < span; i++) {
            sequentialResults[i] = sequentialHistogram.getCount(i);
        }

        //Concurrent Histogram
        ExecutorService pool = new ForkJoinPool(threadCount);
        Future<?>[] myFutures = new Future<?>[span];
        final int perThread = range / threadCount;
        //
        for (int t = 0; t < threadCount; t++) {
            final int from = perThread * t;
            final int to = (t + 1 == threadCount) ? range : perThread * (t + 1);
            myFutures[t] = pool.submit(() -> {
                try {
                    myCyclicBarrier.await();
                    for (int i = from; i < to; i++) {
                        threadSafeHistogram.increment(countFactors(i));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        try {
            for (int t = 0; t < threadCount; t++)
                    myFutures[t].get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }

        for (int i = 0; i < span; i++) {
            concurrentResults[i] = threadSafeHistogram.getCount(i);
        }

        assertArrayEquals(sequentialResults, concurrentResults);
    }

    public static int countFactors(int p) {
        if (p < 2) return 0;
        int factorCount = 1, k = 2;
        while (p >= k * k) {
          if (p % k == 0) {
            factorCount++;
            p= p/k;
          } else 
            k= k+1;
        }
        return factorCount;
      }

}
