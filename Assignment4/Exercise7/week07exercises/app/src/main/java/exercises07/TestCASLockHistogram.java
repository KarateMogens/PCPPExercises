// For week 7
// raup@itu.dk * 10/10/2021
package exercises07;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntToDoubleFunction;

class TestCASLockHistogram {

    // Testing correctness and evaluating performance
    public static void main(String[] args) {


		// Answer to 6.3:
		final int range = 5_000_000;
		final int maxThreads = 16;
		final int bins = 25;

		Histogram2 lockHistogram = new Histogram2(bins); 
		CasHistogram casHistogram = new CasHistogram(bins);
		
		for (int i = 1; i <= maxThreads; i++) {
			final int threads = i;
			Mark7("countPrimeFactors Lock \t threads: " + i + "\t", j -> countParallel(range, threads, lockHistogram));
			Mark7("countPrimeFactors Cas \t threads: " + i + "\t", j -> countParallel(range, threads, casHistogram));
			System.out.println("---");
			for (int k = 0; k < bins; k++) {
				casHistogram.getAndClear(k);
				lockHistogram.getAndClear(k);
			}
		}
    }
    
    // Parallel execution of counting the number of primes for numbers in `range`
   	private static double countParallel(int range, int threadCount, Histogram h) {
		final int perThread = range / threadCount;
		Thread[] threads = new Thread[threadCount];
		for (int t = 0; t < threadCount; t = t + 1) {
			final int from = perThread * t,
					to = (t + 1 == threadCount) ? range : perThread * (t + 1);
			threads[t] = new Thread(() -> {
				for (int i = from; i < to; i++)
					h.increment(countFactors(i));

			});
		}
		for (int t = 0; t < threadCount; t = t + 1)
			threads[t].start();
		try {
			for (int t = 0; t < threadCount; t = t + 1)
				threads[t].join();
		} catch (InterruptedException exn) {
			exn.printStackTrace();
		}
		return h.hashCode(); //To cheat optimizer?

	}

    // Benchmark function
    public static double Mark7(String msg, IntToDoubleFunction f) {
		int n = 10, count = 1, totalCount = 0;
		double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
		do { 
			count *= 2;
			st = sst = 0.0;
			for (int j=0; j<n; j++) {
				Timer t = new Timer();
				for (int i=0; i<count; i++) 
					dummy += f.applyAsDouble(i);
				runningTime = t.check();
				double time = runningTime * 1e9 / count; 
				st += time; 
				sst += time * time;
				totalCount += count;
			}
		} while (runningTime < 0.25 && count < Integer.MAX_VALUE/2);
		double mean = st/n, sdev = Math.sqrt((sst - mean*mean*n)/(n-1));
		System.out.printf("%-25s %15.1f ns %10.2f %10d%n", msg, mean, sdev, count);
		return dummy / totalCount;
    }   


	// Function to count the prime factors of a number `p`
	private static int countFactors(int p) {
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
