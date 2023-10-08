package exercise62;

// Counting primes, using multiple threads for better performance.
// (Much simplified from CountprimesMany.java)
// sestoft@itu.dk * 2014-08-31, 2015-09-15
// modified rikj@itu.dk 2017-09-20
// modified jst@itu.dk 2021-09-24
// raup@itu.dk * 05/10/2022
import java.util.function.IntToDoubleFunction;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import benchmarking.Benchmark;

public class TestCountPrimesThreads {
	public static void main(String[] args) {
		new TestCountPrimesThreads();
	}

	public TestCountPrimesThreads() {
		final int range = 100_000;
		Benchmark.Mark7("countSequential", i -> countSequential(range));
		for (int c = 1; c <= 32; c++) {
			final int threadCount = c;
			//Benchmark.Mark7(String.format("countParallelN %2d", threadCount),
					//i -> countParallelN(range, threadCount));
			//Benchmark.Mark7(String.format("countParallelNLocal %2d", threadCount),
					//i -> countParallelNLocal(range, threadCount));
			Benchmark.Mark7(String.format("countPoolNL %2d", threadCount), i -> countPoolN(range, threadCount));
			Benchmark.Mark7(String.format("countPoollNLocal %2d", threadCount), i -> countPoolNLocal(range, threadCount));
			
		}
	}

	private static boolean isPrime(int n) {
		int k = 2;
		while (k * k <= n && n % k != 0)
			k++;
		return n >= 2 && k * k > n;
	}

	// Sequential solution
	private static long countSequential(int range) {
		long count = 0;
		final int from = 0, to = range;
		for (int i = from; i < to; i++)
			if (isPrime(i))
				count++;
		return count;
	}

	// General parallel solution, using multiple threads
	private static long countParallelN(int range, int threadCount) {
		final int perThread = range / threadCount;
		final AtomicLong lc = new AtomicLong(0);
		Thread[] threads = new Thread[threadCount];
		for (int t = 0; t < threadCount; t++) {
			final int from = perThread * t,
					to = (t + 1 == threadCount) ? range : perThread * (t + 1);
			threads[t] = new Thread(() -> {
				for (int i = from; i < to; i++)
					if (isPrime(i))
						lc.incrementAndGet();
			});
		}
		for (int t = 0; t < threadCount; t++)
			threads[t].start();
		try {
			for (int t = 0; t < threadCount; t++)
				threads[t].join();
			// System.out.println("Primes: "+lc.get());
		} catch (InterruptedException exn) {
		}
		return lc.get();
	}

	// General parallel solution, using multiple threads
	private static long countParallelNLocal(int range, int threadCount) {
		final int perThread = range / threadCount;
		final long[] results = new long[threadCount];
		Thread[] threads = new Thread[threadCount];
		for (int t = 0; t < threadCount; t++) {
			final int from = perThread * t,
					to = (t + 1 == threadCount) ? range : perThread * (t + 1);
			final int threadNo = t;
			threads[t] = new Thread(() -> {
				long count = 0;
				for (int i = from; i < to; i++)
					if (isPrime(i))
						count++;
				results[threadNo] = count;
			});
		}
		for (int t = 0; t < threadCount; t++)
			threads[t].start();
		try {
			for (int t = 0; t < threadCount; t++)
				threads[t].join();
		} catch (InterruptedException exn) {
		}
		long result = 0;
		for (int t = 0; t < threadCount; t++)
			result += results[t];
		return result;
	}

	private static long countPoolN(int range, int threadCount) {

		ExecutorService pool = new ForkJoinPool(threadCount);
		Future<?>[] myFutures = new Future<?>[threadCount];
		final int perThread = range / threadCount;
		final AtomicLong lc = new AtomicLong(0);

		for (int t = 0; t < threadCount; t++) {
			final int from = perThread * t;
			final int to = (t + 1 == threadCount) ? range : perThread * (t + 1);
			myFutures[t] = pool.submit(() -> {
				for (int i = from; i < to; i++)
					if (isPrime(i))
						lc.incrementAndGet();
				});
		}

		try {
			for (int t = 0; t < threadCount; t++)
				myFutures[t].get();
			// System.out.println("Primes: "+lc.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			pool.shutdown();
		}
		return lc.get();
	}

	private static long countPoolNLocal(int range, int threadCount) {

		ExecutorService pool = new ForkJoinPool(threadCount);
		Future<?>[] myFutures = new Future<?>[threadCount];
		final int perThread = range / threadCount;
		long[] myLongs = new long[threadCount];

		for (int t = 0; t < threadCount; t++) {
			final int from = perThread * t;
			final int to = (t + 1 == threadCount) ? range : perThread * (t + 1);
			myFutures[t] = pool.submit(() -> {
				long result = 0;
				for (int i = from; i < to; i++) {
					if (isPrime(i)) {
						result++;
					}
				}
				return result;
			});
		}
		try {
			for (int t = 0; t < threadCount; t++)
				myLongs[t] = (long) myFutures[t].get();
			// System.out.println("Primes: "+lc.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			pool.shutdown();
		}
		long result = 0;
		for (int t = 0; t < threadCount; t++)
			result += myLongs[t];
		return result;
	}
}
