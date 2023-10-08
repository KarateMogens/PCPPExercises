package exercise63;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import benchmarking.Benchmark;

public class BenchmarkHistogram {

    public static void main(String[] args) {
        BenchmarkHistogram myBenchmarkHistogram = new BenchmarkHistogram();
    }

    public BenchmarkHistogram() {

        final int range = 4_999_999;
        final int span = 25;
       
        for (int c = 1; c <= 32; c++) {
			final int threadCount = c;
            final int locks = c;
            Histogram normalHistogram = new Histogram2(span);
            Histogram stripHistogram = new Histogram3(span, locks);
            Benchmark.Mark7(String.format("countPrimeFactor %2d", threadCount), i -> countPrimeFactors(normalHistogram, threadCount, range));
            Benchmark.Mark7(String.format("countPrimeFactorStripe %2d", threadCount), i -> countPrimeFactorsStrip(stripHistogram, threadCount, range));
        }


    }


    public float countPrimeFactorsStrip(Histogram histogram, int threadCount, int range) {

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
        return histogram.getCount(1);

    }

    public float countPrimeFactors(Histogram histogram, int threadCount, int range) {
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
        return histogram.getCount(1);
        
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
