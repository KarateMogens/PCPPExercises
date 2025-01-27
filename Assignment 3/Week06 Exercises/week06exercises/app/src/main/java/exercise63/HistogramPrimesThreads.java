package exercise63;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

// first version by Kasper modified by jst@itu.dk 24-09-2021
// raup@itu.dk * 05/10/2022
// jst@itu.dk 22-09-2023

public class HistogramPrimesThreads {
    public static void main(String[] args) { new HistogramPrimesThreads(); }

    public HistogramPrimesThreads() { 
      // TODO: Replace below with an instance of Histogram2 exercise 6.3.1 (recall that Histogram1 is not thread-safe)
      final Histogram histogram = new Histogram2(25); // 25 bins sufficient for a range of 0..4_999_999
      //final Histogram histogram = new Histogram3(25, 10);

      // TODO: Run it using multiple threads, and check the countFactors function below (it might be useful)
      final int threadCount = 10;
      final int range = 4_999_999;

      ExecutorService pool = new ForkJoinPool(threadCount);
		  Future<?>[] myFutures = new Future<?>[threadCount];
		  final int perThread = range / threadCount;

      for (int t = 0; t < threadCount; t++) {
        final int from = perThread * t;
        final int to = (t + 1 == threadCount) ? range : perThread * (t + 1);
        myFutures[t] = pool.submit(() -> {
          for (int i = from; i < to; i++) {
            histogram.increment(countFactors(i));
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

      // Finally we plot the result to ensure that it looks as expected (see example output in the exercise script)
      dump(histogram);
    }

    // Returns the number of prime factors of `p`
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

    public static void dump(Histogram histogram) {
      for (int bin= 0; bin < histogram.getSpan(); bin= bin+1) {
        System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
    }
  }
}

