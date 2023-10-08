## Exercise 6.1 

This exercise is based on the program AccountExperiments.java (in the exercises direc- tory for week 6). It generates a number of transactions to move money between accounts. Each transaction simulate transaction time by sleeping 50 milliseconds. The transactions are randomly generated, but ensures that the source and target accounts are not the same.

---

1. Use Mark7 (from Benchmark.java in the benchmarking package) to measure the execution time and verify that the time it takes to run the program is proportional to the transaction time.

|   msg |   average |   Std Deviation   |   Iterations    |
| --- | --- | --- | --- |
|   do 1 transactions:  |   53270367,7 ns   |    674073,12   |   8   |
|   do 2 transactions:  |   107480873,9 ns  |    1158535,86  |   4   |   
|   do 4 transactions:  |   213712785,4 ns  |   2287404,52   |   2   |
|   do 8 transactions:  |   428151556,3 ns  |   2086150,00   |   2   |
|   do 16 transactions: |   858329293,6 ns  |    3915457,27  |   2   |
|   do 32 transactions: |   1707435760,5 ns |   11077163,46  |   2   |

#### Answer:

> From the above output, we can see that indeed the execution time is linearly proportional to the number of transactions, as a doubling of transactions results in a doubling of the execution time.

2. Now consider the version in ThreadsAccountExperimentsMany.java (in the directory exercise61).

Consider these four lines of the transfer:

    Account min = accounts[Math.min(source.id, target.id)];
    Account max = accounts[Math.max(source.id, target.id)];
    synchronized(min){
        synchronized(max){
            // do transaction
        }
    }

Explain why the calculation of min and max are necessary? Eg. what could happen if the code was written
like this:

    Account s = accounts[source.id];
    Account t = accounts[target.id];
    synchronized(s){ //      (1)
        synchronized(t){ //  (2)
            //do transaction
        }
    }


Run the program with both versions of the code shown above and explain the results of doing this:

#### Answer:

> Consider the interleaving involving two accounts; Acc1 and Acc2. 
> 
>If we have a transaction where Accc1 sends a transaction to Acc2, and Acc2 sends a transaction to Acc1 simultaneously.
>
>Then we can have the interleaving Acc1(1), Acc2(1) which results in a deadlock. Acc1 cannot release the intrinsic lock until Acc2 releases, and Acc2 cannot release until Acc1 releases.
>
>With the initial implementation, where we always lock on the minimum of the two first, the interleaving above will not result in a deadlock.

---


3. Change the program in ThreadsAccountExperimentsMany.java to use a the executor framework instead of raw threads. Make it use a ForkJoin thread pool. For now do not worry about terminating the main thread, but insert a print statement in the doTransaction method, so you can see that all executors are active.

#### Answer:

        public ThreadsAccountExperimentsMany() {
            for (int i = 0; i < N; i++) {
                accounts[i] = new Account(i);
        }

        ExecutorService pool = new ForkJoinPool();

        Future<?>[] myFutures = new Future[NO_THREADS];
        
        for (int i = 0; i < NO_THREADS; i++) {
            myFutures[i] = pool.submit(() -> doNTransactions(NO_TRANSACTION)); // A new runnable that is a lambda containing our simple method. Java infers that it is runnable/callable.
        }

        for (int i = 0; i < NO_THREADS; i++) {
            try {
                myFutures[i].get();
            } catch (InterruptedException e) {
                System.out.println("At i = " + i + " I got error: " + e.getCause());
                System.exit(0);
            } catch (ExecutionException e) {
                System.out.println("At i = " + i + " I got error: " + e.getCause());
                System.exit(0);
            }
        }

        pool.shutdown();

4. Ensure that the executor shuts down after all tasks has been executed. Hint: See slides for suggestions on how to wait until all tasks are finished.

#### Answer:

> See code above.

--- 
 
### Exercise 6.2 

Use the code in file `TestCountPrimesThreads.java` (in the exercises directory for week 6) to count prime numbers using threads.

1. Report and comment on the results you get from running `TestCountPrimesThreads.java`.

        OS:   Mac OS X; 14.0; aarch4 
        JVM:  Eclipse Adoptium; 11.0.17
        CPU:  null; 8 "cores"
        Date: 2023-10-06T16:28:27+0200
        countSequential                 2632214,7 ns   11119,10        128
        countParallelN  1               2676227,7 ns   22437,54        128
        countParallelNLocal  1          2675214,0 ns   11352,16        128
        countParallelN  2               1727483,1 ns    4460,62        256
        countParallelNLocal  2          1730406,6 ns    1922,58        256
        countParallelN  3               1300161,6 ns    8507,77        256
        countParallelNLocal  3          1292929,5 ns    6135,32        256
        countParallelN  4               1071432,0 ns    4774,96        256
        countParallelNLocal  4          1060472,7 ns    6707,30        256
        countParallelN  5               1065892,5 ns    5863,34        256
        countParallelNLocal  5          1030421,0 ns    3445,57        256
        countParallelN  6               1039193,7 ns    3217,85        256
        countParallelNLocal  6           986613,9 ns    2653,86        256
        countParallelN  7                995744,9 ns    2170,83        256
        countParallelNLocal  7           954983,5 ns   11161,87        512
        countParallelN  8                998839,0 ns    5114,32        256
        countParallelNLocal  8           966506,2 ns   10309,00        512
        countParallelN  9                991524,4 ns    2230,84        256
        countParallelNLocal  9           960699,5 ns    7003,31        512
        countParallelN 10               1003988,9 ns    3876,19        256
        countParallelNLocal 10           983588,8 ns    3527,92        256
        countParallelN 11               1002295,4 ns    2622,01        256
        countParallelNLocal 11           978659,0 ns    2371,77        256
        countParallelN 12                999517,4 ns    2546,86        256
        countParallelNLocal 12           983822,4 ns    3509,13        256
        countParallelN 13               1016015,1 ns    9370,41        256
        countParallelNLocal 13           980489,4 ns   13595,59        256
        countParallelN 14               1029106,3 ns   22955,15        256
        countParallelNLocal 14           997807,8 ns   22701,98        256
        countParallelN 15               1040775,6 ns   25879,81        256
        countParallelNLocal 15          1011664,5 ns   24016,37        256
        countParallelN 16               1056128,6 ns   28875,74        256
        countParallelNLocal 16          1021091,5 ns   30117,40        256
        countParallelN 17               1067482,9 ns   31918,71        256
        countParallelNLocal 17          1049715,6 ns   30936,98        256
        countParallelN 18               1106867,0 ns   30565,53        256
        countParallelNLocal 18          1063473,1 ns    6497,85        256
        countParallelN 19               1113058,7 ns    5098,87        256
        countParallelNLocal 19          1086570,2 ns    3613,91        256
        countParallelN 20               1147052,3 ns   34391,23        256
        countParallelNLocal 20          1128145,7 ns   23699,41        256
        countParallelN 21               1173946,6 ns   30812,92        256
        countParallelNLocal 21          1157767,8 ns   33425,09        256
        countParallelN 22               1188580,6 ns    8532,20        256
        countParallelNLocal 22          1157783,8 ns    6559,02        256
        countParallelN 23               1221616,6 ns   31126,23        256
        countParallelNLocal 23          1195834,4 ns   35620,53        256
        countParallelN 24               1232408,7 ns   10108,74        256
        countParallelNLocal 24          1210216,9 ns    6987,62        256
        countParallelN 25               1265360,1 ns   34354,34        256
        countParallelNLocal 25          1248873,7 ns   29885,51        256
        countParallelN 26               1311203,1 ns   35601,51        256
        countParallelNLocal 26          1267977,1 ns   37040,19        256
        countParallelN 27               1315165,6 ns   37272,24        256
        countParallelNLocal 27          1279314,7 ns   10081,43        256
        countParallelN 28               1397527,1 ns  122547,86        256
        countParallelNLocal 28          1319694,3 ns   41616,32        256
        countParallelN 29               1353403,2 ns    8352,70        256
        countParallelNLocal 29          1344022,6 ns   38072,33        256
        countParallelN 30               1385807,4 ns   37334,16        256
        countParallelNLocal 30          1369012,5 ns   56208,16        256
        countParallelN 31               1463538,8 ns   77606,19        256
        countParallelNLocal 31          1380192,0 ns    4131,93        256
        countParallelN 32               1430483,7 ns   35990,33        256
        countParallelNLocal 32          1407647,5 ns   10942,60        256

2. Rewrite `TestCountPrimesthreads.java` using Futures for the tasks of each of the threads in part 1. Run your solutions and report results. How do they compare with the results from the version using threads?


3. Change the program in ThreadsAccountExperimentsMany.java to use a the executor framework instead of raw threads. Make it use a ForkJoin thread pool. For now do not worry about terminating the main thread, but insert a print statement in the doTransaction method, so you can see that all executors are active.

#### Answer:

> See code: `TestCountPrimesThreads.java`

4. Ensure that the executor shuts down after all tasks has been executed. 

- Hint: See slides for suggestions on how to wait until all tasks are finished.

#### Answer:

> See code: `TestCountPrimesThreads.java`

---

### Exercise 6.3

A histogram is a collection of bins, each of which is an integer count. The span of the histogram is the number of bins. In the problems below a span of 30 will be sufficient; in that case the bins are numbered 0...29.

Consider this Histogram interface for creating histograms:

    interface Histogram {
        public void increment(int bin);
        public float getPercentage(int bin);
        public int getSpan();
    }

Method call `increment(7)` will add one to bin 7; method call `getCount(7)` will return the current count in bin 7; method call `getPercentage(7)` will return the current percentage of total in bin 7; method `getSpan()` will return the number of bins; method call `getTotal()` will return the current total of all bins.

There is a non-thread-safe implementation of `Histogram1` in file `SimpleHistogram.java`. You may assume that the dump method given there is called only when no other thread manipulates the histogram and therefore does not require locking, and that the span is fixed (immutable) for any given Histogram object.

1. Make a thread-safe implementation, class `Histogram2` implementing the interface `Histogram`. Use suitable modifiers (final and synchronized) in a copy of the Histogram1 class. This class must use at most one lock to ensure mutual exclusion.
Explain what fields and methods need modifiers and why. Does the getSpan method need to be synchro- nized?

#### Answer:
> 

2. Now create a new class, `Histogram3` (implementing the Histogram interface) that uses lock striping. You can start with a copy of `Histogram2`. Then, the constructor of `Histogram3` must take an additional parameter `nrLocks` which indicates the number of locks that the histogram uses. You will have to associate a lock to each bin. Note that, if the number of locks is less than the number of bins, you may use the same lock for more than one bin. Try to distribute locks evenly among bins; consider the modulo operation % for this task.


#### Answer:
>

3. Now consider again counting the number of prime factors in a number p. Use the `Histogram2` class to write a program with multiple threads that counts how many numbers in the range 0. . . 4 999 999 have 0 prime factors, how many have 1 prime factor, how many have 2 prime factors, and so on. You may draw inspiration from the `TestCountPrimesThreads.java`.

- Hint: There is a class HistogramPrimesThread.java which you can use a starting point for this exercise. That class contains a method countFactors(int p) which returns the number of prime factors of p. This might be handy for the exercise.

#### Answer:
> See code: `HistogramPrimesThreads.java`

4. Finally, evaluate the effect of lock striping on the performance of question 6.3. Create a new class where you use Mark7 to measure the performance of `Histogram3` with increasing number of locks to compute the number of prime factors in 0. . . 4 999 999. Report your results and comment on them. Is there a increase or not? Why?

#### Results:

    countPrimeFactor  1          1404794366,7 ns 4556421,88          2
    countPrimeFactorStrip  1     1475463183,3 ns 2428675,16          2
    countPrimeFactor  2           984323779,1 ns 4684187,88          2
    countPrimeFactorStrip  2      947959579,3 ns 5636698,35          2
    countPrimeFactor  3           778095604,2 ns 2751872,83          2
    countPrimeFactorStrip  3      704127356,3 ns 3403446,34          2
    countPrimeFactor  4           762312631,3 ns 6965336,74          2
    countPrimeFactorStrip  4      611134650,0 ns 30622612,30          2
    countPrimeFactor  5           995805462,6 ns 14227365,21          2
    countPrimeFactorStrip  5      643604548,0 ns 8704707,98          2
    countPrimeFactor  6           970381939,6 ns 8038769,04          2
    countPrimeFactorStrip  6      641543347,8 ns 12663950,75          2
    countPrimeFactor  7          1002830089,6 ns 4626686,00          2
    countPrimeFactorStrip  7      658673266,7 ns 12556435,12          2
    countPrimeFactor  8           995867752,1 ns 9976077,20          2
    countPrimeFactorStrip  8      667775404,2 ns 10391047,53          2
    countPrimeFactor  9           970210200,0 ns 10002781,97          2
    countPrimeFactorStrip  9      674087235,5 ns 16441226,94          2
    countPrimeFactor 10          1115823245,9 ns 29750654,35          2
    countPrimeFactorStrip 10      704894025,1 ns 7508765,06          2
    countPrimeFactor 11          1088537389,6 ns 45210617,47          2
    countPrimeFactorStrip 11      689926912,4 ns 20676936,57          2
    countPrimeFactor 12          1076810499,9 ns 11553216,69          2
    countPrimeFactorStrip 12      699005635,4 ns 19590977,08          2
    countPrimeFactor 13          1047648598,0 ns 10066040,36          2
    countPrimeFactorStrip 13      712611964,6 ns 12854858,10          2
    countPrimeFactor 14          1056978595,8 ns 10227285,14          2
    countPrimeFactorStrip 14      707827225,1 ns 15906513,76          2
    countPrimeFactor 15          1022163722,9 ns 13590673,56          2
    countPrimeFactorStrip 15      707051300,0 ns 12975955,53          2
    countPrimeFactor 16          1106642974,9 ns 20072457,06          2
    countPrimeFactorStrip 16      710817468,8 ns 21487187,99          2


#### Answer:

> See code: `BenchmarkHistogram.java` for implementation.
> 