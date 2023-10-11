// For week 7
// raup@itu.dk * 10/10/2021
package exercises07;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntToDoubleFunction;

class TestCASLockHistogram {
    // Testing correctness and evaluating performance
public static void main(String[] args) {
	CasHistogram histogramCAS = new CasHistogram(25);
	Histogram2 histogramLock = new Histogram2(25);

	final int range= 100_000;

	for (int c= 1; c<=32; c++) {
		final int threadCount = c;
		Mark7(String.format("histogramLock", threadCount),
				i -> {countParallel(range, threadCount, histogramLock); return 0.0;});}
	for (int c= 1; c<=32; c++) {
		final int threadCount = c;
		Mark7(String.format("histogramCAS", threadCount),
				i -> {countParallel(range, threadCount, histogramCAS); return 0.0;});}

// Is it spinning threads? 
// Why returning 0.0?
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

    // Parallel execution of counting the number of primes for numbers in `range`
    private static void countParallel(int range, int threadCount, Histogram h) {
		final int perThread= range / threadCount;
		Thread[] threads= new Thread[threadCount];
		for (int t=0; t<threadCount; t= t+1) {
			final int from= perThread * t, 
				to= (t+1==threadCount) ? range : perThread * (t+1); 
			threads[t]= new Thread( () -> {
					for (int i= from; i<to; i++) h.increment(countFactors(i));
                
			});
		} 
		for (int t= 0; t<threadCount; t= t+1) 
			threads[t].start();
		try {
			for (int t= 0; t<threadCount; t= t+1) 
				threads[t].join();
		} catch (InterruptedException exn) { }
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
}

// histogramLock                   7652649,7 ns   68557,07         64
// histogramLock                   8483314,5 ns   35903,00         32
// histogramLock                  12958787,4 ns   84681,28         32
// histogramLock                  15460790,8 ns  443126,89         32
// histogramLock                  15718773,2 ns  162109,72         16
// histogramLock                  15642158,6 ns   66280,16         16
// histogramLock                  15863208,6 ns  387514,56         16
// histogramLock                  15967764,1 ns  253158,40         16
// histogramLock                  16043618,0 ns  485470,13         16
// histogramLock                  16235950,3 ns  439787,17         16
// histogramLock                  16152142,7 ns  304525,38         16
// histogramLock                  16151243,8 ns  435834,38         16
// histogramLock                  15845945,6 ns  200403,34         16
// histogramLock                  15839116,4 ns  185760,61         16
// histogramLock                  16505594,3 ns 1168562,45         16
// histogramLock                  18800057,0 ns  892834,09         16
// histogramLock                  18107606,8 ns  185791,34         16
// histogramLock                  18134623,4 ns  183964,88         16
// histogramLock                  18129470,3 ns  210159,26         16
// histogramLock                  18098569,8 ns   47621,46         16
// histogramLock                  18081319,0 ns   25419,57         16
// histogramLock                  18074048,2 ns   32724,80         16
// histogramLock                  18358865,9 ns  430318,92         16
// histogramLock                  18424438,5 ns  627251,39         16
// histogramLock                  18322201,6 ns  284184,27         16
// histogramLock                  18293496,4 ns  244337,96         16
// histogramLock                  18414556,8 ns  276110,20         16
// histogramLock                  18750765,4 ns  799537,03         16
// histogramLock                  18194738,6 ns  193200,46         16
// histogramLock                  18125006,5 ns   72280,16         16
// histogramLock                  18501506,0 ns  308393,76         16
// histogramLock                  18638064,3 ns  569496,64         16
// histogramCAS                    7652146,6 ns   36272,85         64
// histogramCAS                    4909262,3 ns   32241,66         64
// histogramCAS                    3907286,4 ns   11849,50         64
// histogramCAS                    3249024,9 ns   54229,89        128
// histogramCAS                    4082634,1 ns  129279,41         64
// histogramCAS                    4179390,2 ns   31359,14         64
// histogramCAS                    4324670,9 ns   59559,68         64
// histogramCAS                    4617145,7 ns   45567,18         64
// histogramCAS                    4919237,4 ns  150268,33         64
// histogramCAS                    4903902,7 ns   73409,95         64
// histogramCAS                    4957966,1 ns   33731,58         64
// histogramCAS                    5051124,2 ns   42412,26         64
// histogramCAS                    5056991,6 ns   79935,56         64
// histogramCAS                    5086702,2 ns  132631,37         64
// histogramCAS                    6084752,7 ns   84158,62         64
// histogramCAS                    6050168,0 ns   84812,59         64
// histogramCAS                    5959846,7 ns   97364,88         64
// histogramCAS                    6123187,4 ns  100507,10         64
// histogramCAS                    6179484,4 ns   76646,95         64
// histogramCAS                    6143295,9 ns   89029,51         64
// histogramCAS                    6312497,7 ns  130619,76         64
// histogramCAS                    6386518,9 ns   69351,28         64
// histogramCAS                    6341096,8 ns  119457,85         64
// histogramCAS                    6280141,0 ns   95728,92         64
// histogramCAS                    6311146,6 ns   99407,59         64
// histogramCAS                    6350526,6 ns  225997,80         64
// histogramCAS                    6327174,4 ns  115264,53         64
// histogramCAS                    6305524,9 ns   99058,90         64
// histogramCAS                    6262211,2 ns   84569,24         64
// histogramCAS                    6166360,4 ns  197606,61         64
// histogramCAS                    6240888,9 ns   74727,81         64
// histogramCAS                    6321974,0 ns   92474,05         64
