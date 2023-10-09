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


3. Change the program in `ThreadsAccountExperimentsMany.java` to use a the executor framework instead of raw threads. Make it use a `ForkJoin` thread pool. For now do not worry about terminating the main thread, but insert a print statement in the `doTransaction` method, so you can see that all executors are active.

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

        }

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

#### Answer:

> The execution time for the method countParallelNLocal is consistenly slightly faster than countParallelN (by about 5%). `CountParallelN` uses a shared, synchronized datastructure (`AtomicLong`) to store intermediate values. This also means that the `AtomicLong` will block threads from updating the variable at the same time, thus slowing down the program. `CountParallelNLocal` instead uses a datastructure that does not exclude threads from updating the values at the same time, thus avoiding interleavings where threads slow each other down.
> We see the execution time for both methods converge around 8-threads, before the execution times start increasing again. We assume that the performance drops because the work to be carried out becomes too sub-divided, meaning that the overhead of starting up a thread becomes too large relative to the work to be carried out.

2. Rewrite `TestCountPrimesthreads.java` using Futures for the tasks of each of the threads in part 1. Run your solutions and report results. How do they compare with the results from the version using threads?

        countSequential                 2657251,9 ns    5762,41        128
        countParallelN  1               2734148,6 ns    7918,85        128
        countParallelNLocal  1          2705608,7 ns    5461,28        128
        countPoolN  1                   2749803,2 ns   56788,29        128
        countPoollNLocal  1             2725307,3 ns    8648,93        128
        countParallelN  2               1741908,4 ns    5693,96        256
        countParallelNLocal  2          1744840,5 ns    4351,03        256
        countPoolN  2                   1765988,2 ns    4900,49        256
        countPoollNLocal  2             1760950,6 ns    8228,54        256
        countParallelN  3               1304276,9 ns    3174,14        256
        countParallelNLocal  3          1294779,1 ns    9989,86        256
        countPoolN  3                   1343901,0 ns   10173,78        256
        countPoollNLocal  3             1330535,4 ns    1221,17        256
        countParallelN  4               1071959,3 ns    1147,62        256
        countParallelNLocal  4          1062731,7 ns    9562,54        256
        countPoolN  4                   1147898,6 ns   30479,64        256
        countPoollNLocal  4             1135602,2 ns   24525,04        256
        countParallelN  5               1093025,3 ns   25265,17        256
        countParallelNLocal  5          1044002,4 ns   23306,74        256
        countPoolN  5                   1182108,5 ns   51677,70        256
        countPoollNLocal  5             1069537,4 ns    6029,95        256
        countParallelN  6               1049195,7 ns    4215,31        256
        countParallelNLocal  6           999685,0 ns    5745,27        256
        countPoolN  6                   1190947,5 ns   31618,45        256
        countPoollNLocal  6             1117278,1 ns   22177,21        256
        countParallelN  7               1302600,7 ns  439149,53        256
        countParallelNLocal  7           982497,3 ns   16866,38        512
        countPoolN  7                   1165626,2 ns   19019,46        256
        countPoollNLocal  7             1105390,9 ns   32412,41        256
        countParallelN  8               1007349,2 ns    8008,79        256
        countParallelNLocal  8           970667,8 ns    8334,41        512
        countPoolN  8                   1100943,7 ns    5829,31        256
        countPoollNLocal  8             1047030,7 ns   20727,06        256
        countParallelN  9               1005898,1 ns   22619,05        256
        countParallelNLocal  9           973668,8 ns   19680,85        256
        countPoolN  9                   1084381,7 ns   29087,34        256
        countPoollNLocal  9             1024874,3 ns   25143,40        256
        countParallelN 10               1014133,5 ns   22750,14        256
        countParallelNLocal 10           985228,7 ns    9479,57        512
        countPoolN 10                   1097397,5 ns   22459,75        256
        countPoollNLocal 10             1022976,9 ns   22489,26        256
        countParallelN 11               1011385,4 ns   25656,55        256
        countParallelNLocal 11           986153,8 ns   21378,86        256
        countPoolN 11                   1092871,3 ns   23880,98        256
        countPoollNLocal 11             1013779,7 ns    5534,71        256
        countParallelN 12               1010504,8 ns   15877,28        256
        countParallelNLocal 12           980918,3 ns   11005,71        256
        countPoolN 12                   1070624,4 ns    6281,46        256
        countPoollNLocal 12             1006139,8 ns   19319,79        256
        countParallelN 13               1033118,3 ns   29280,35        256
        countParallelNLocal 13           984085,7 ns    7450,89        256
        countPoolN 13                   1056182,7 ns    9331,90        256
        countPoollNLocal 13              980604,9 ns   13345,25        512
        countParallelN 14               1024976,7 ns    2941,95        256
        countParallelNLocal 14          1011687,6 ns   31619,02        256
        countPoolN 14                   1052957,5 ns   26902,81        256
        countPoollNLocal 14              974426,4 ns   32337,56        256
        countParallelN 15               1070347,3 ns   33983,73        256
        countParallelNLocal 15          1046592,4 ns   23529,96        256
        countPoolN 15                   1046905,3 ns   29007,97        256
        countPoollNLocal 15              958711,2 ns   19507,63        512
        countParallelN 16               1086328,9 ns   32556,82        256
        countParallelNLocal 16          1052847,4 ns   35780,39        256
        countPoolN 16                   1045275,6 ns   21528,86        256
        countPoollNLocal 16              963886,7 ns   33983,63        256
        countParallelN 17               1114513,5 ns   49967,08        256
        countParallelNLocal 17          1100516,5 ns   55656,34        256
        countPoolN 17                   1196207,9 ns  219969,13        256
        countPoollNLocal 17              963757,3 ns   18993,16        512
        countParallelN 18               1109875,0 ns    6923,02        256
        countParallelNLocal 18          1106920,5 ns   41563,98        256
        countPoolN 18                   1050633,1 ns   32701,17        256
        countPoollNLocal 18              962532,4 ns   16009,51        512
        countParallelN 19               1144618,3 ns   37121,20        256
        countParallelNLocal 19          1113909,8 ns   35520,18        256
        countPoolN 19                   1056174,5 ns   30788,18        256
        countPoollNLocal 19              985039,5 ns   44553,23        256
        countParallelN 20               1213133,3 ns   87611,07        256
        countParallelNLocal 20          1172589,0 ns   73939,24        256
        countPoolN 20                   1040545,5 ns    8414,33        256
        countPoollNLocal 20              952022,4 ns   20574,60        512
        countParallelN 21               1201539,0 ns   35592,09        256
        countParallelNLocal 21          1198400,4 ns   90473,43        256
        countPoolN 21                   1056863,3 ns   36149,24        256
        countPoollNLocal 21              964415,3 ns   38337,54        512
        countParallelN 22               1198041,1 ns   22851,41        256
        countParallelNLocal 22          1175563,9 ns    9122,93        256
        countPoolN 22                   1052430,0 ns   16189,11        256
        countPoollNLocal 22              954384,4 ns   18256,62        512
        countParallelN 23               1226773,6 ns   35347,54        256
        countParallelNLocal 23          1203903,5 ns   32907,72        256
        countPoolN 23                   1067805,6 ns   30674,28        256
        countPoollNLocal 23             1004732,2 ns   42781,50        256
        countParallelN 24               1232474,2 ns    8513,64        256
        countParallelNLocal 24          1206048,4 ns    6726,03        256
        countPoolN 24                   1061848,8 ns    3572,69        256
        countPoollNLocal 24              958268,4 ns   17039,66        512
        countParallelN 25               1267541,5 ns   41163,50        256
        countParallelNLocal 25          1255453,5 ns   36110,81        256
        countPoolN 25                   1051630,2 ns    9072,35        256
        countPoollNLocal 25              964160,9 ns   14826,16        512
        countParallelN 26               1321417,2 ns   88293,59        256
        countParallelNLocal 26          1279793,5 ns   35532,33        256
        countPoolN 26                   1062830,2 ns   25374,04        256
        countPoollNLocal 26             1027921,9 ns  116852,77        512
        countParallelN 27               1339013,9 ns   42206,72        256
        countParallelNLocal 27          1300909,7 ns   34897,90        256
        countPoolN 27                   1058535,7 ns   31395,27        256
        countPoollNLocal 27             1004169,0 ns   32289,55        256
        countParallelN 28               1364234,8 ns   54661,78        256
        countParallelNLocal 28          1310863,2 ns   10955,37        256
        countPoolN 28                   1119491,8 ns   50210,91        256
        countPoollNLocal 28              967032,7 ns   20354,65        256
        countParallelN 29               1367615,3 ns   46344,81        256
        countParallelNLocal 29          1339252,8 ns   12555,91        256
        countPoolN 29                   1055898,2 ns    9095,55        256
        countPoollNLocal 29              964098,8 ns   23686,44        512
        countParallelN 30               1403977,9 ns   50440,42        256
        countParallelNLocal 30          1371445,1 ns   38265,69        256
        countPoolN 30                   1054550,1 ns    6266,31        256
        countPoollNLocal 30              959998,7 ns   14584,12        512
        countParallelN 31               1401890,7 ns   37804,91        256
        countParallelNLocal 31          1393705,8 ns   31300,76        256
        countPoolN 31                   1052780,3 ns    7828,81        256
        countPoollNLocal 31              963609,9 ns   16915,45        512
        countParallelN 32               1431400,9 ns   41151,21        256
        countParallelNLocal 32          1416982,6 ns   28284,29        256
        countPoolN 32                   1055673,5 ns   10346,46        256
        countPoollNLocal 32              954994,5 ns   14920,24        512

#### Answer:

> With a program that simple uses pools, in a similar manner than if they were threads, we see that pools are slower than the programs using raw threads until around a threadcount of around 12/13. We are unsure of why `countPoolNLocal` becomes faster at a higher thread count, as it goes against what we could expect from the results presented during the lecture.
> It is possible that the structure used in the program is unsuited for the specific executor `ForkJoinPool()`, and that instead a threshold-parameter should be used, with recursive submission of tasks. However, we understood the assignment as having to be implemented with the structure provided.


3. Change the program in ThreadsAccountExperimentsMany.java to use a the executor framework instead of raw threads. Make it use a ForkJoin thread pool. For now do not worry about terminating the main thread, but insert a print statement in the doTransaction method, so you can see that all executors are active.

#### Answer:

> See code: `ThreadsAccountExperimentsMany.java`

4. Ensure that the executor shuts down after all tasks has been executed. 

- Hint: See slides for suggestions on how to wait until all tasks are finished.

#### Answer:

> See code: `ThreadsAccountExperimentsMany.java`

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
Explain what fields and methods need modifiers and why. Does the getSpan method need to be synchronized?

#### Answer:
> #### Fields: 
> We have declared both fields `counts` and `total` as `private` to ensure that we don't have escaping. Furthermore, we have declared `counts` to final, to ensure safe publication. We do not need to do this for `total`, because it is initialized to a default value.

>Our methods `increment()`, `getpercentage()` and `getCount()` are declared to be synchronized, to use the intrinsic object lock. This is to ensure mutual exclusivity on the shared data `counts` and `total` and therefore ensure that we do not have a datarace. 
> Note that the getSpan does not need to be synchronized, because the array `count` is initialized in the constructor, and is final meaning that the reference to it does not change after being initialized. It will therefore never have a different size during runtime.

2. Now create a new class, `Histogram3` (implementing the Histogram interface) that uses lock striping. You can start with a copy of `Histogram2`. Then, the constructor of `Histogram3` must take an additional parameter `nrLocks` which indicates the number of locks that the histogram uses. You will have to associate a lock to each bin. Note that, if the number of locks is less than the number of bins, you may use the same lock for more than one bin. Try to distribute locks evenly among bins; consider the modulo operation % for this task.


#### Answer:
> See code: `Histogram3.java`

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


#### Results:

    countPrimeFactorStripe  1    1271908100,0 ns 31706472,30          2
    countPrimeFactorStripe  2     895551608,3 ns 35093806,79          2
    countPrimeFactorStripe  3     742946864,6 ns 21497125,28          2
    countPrimeFactorStripe  4     683758704,3 ns 12349864,52          2
    countPrimeFactorStripe  5     669471724,9 ns 15636630,31          2
    countPrimeFactorStripe  6     683527718,8 ns 12713200,02          2
    countPrimeFactorStripe  7     680696998,0 ns 12796805,24          2
    countPrimeFactorStripe  8     685526714,7 ns 12253135,24          2
    countPrimeFactorStripe  9     696781127,2 ns 14999454,30          2
    countPrimeFactorStripe 10     687778320,8 ns 12663868,31          2
    countPrimeFactorStripe 11     681821093,8 ns 7807587,78          2
    countPrimeFactorStripe 12     720066946,0 ns 75278453,14          2
    countPrimeFactorStripe 13     681739533,4 ns 11843746,43          2
    countPrimeFactorStripe 14     696951062,5 ns 24672894,94          2
    countPrimeFactorStripe 15     730652133,3 ns 74126335,55          2
    countPrimeFactorStripe 16     698379481,3 ns 16988953,42          2
    countPrimeFactorStripe 17     744764343,8 ns 78213007,51          2
    countPrimeFactorStripe 18     763513929,2 ns 58268861,23          2
    countPrimeFactorStripe 19     700840483,3 ns 12614169,86          2
    countPrimeFactorStripe 20     698226820,8 ns 10991220,48          2
    countPrimeFactorStripe 21     711197512,6 ns 31282926,16          2
    countPrimeFactorStripe 22     701132947,9 ns 18524572,03          2
    countPrimeFactorStripe 23     718444891,6 ns 16691863,52          2
    countPrimeFactorStripe 24     704990331,3 ns 35441904,88          2
    countPrimeFactorStripe 25     798982760,4 ns 76739694,92          2
    countPrimeFactorStripe 26     745213108,4 ns 14266686,24          2
    countPrimeFactorStripe 27     743640893,7 ns 19318868,50          2
    countPrimeFactorStripe 28     736084575,0 ns 29249103,28          2
    countPrimeFactorStripe 29     710871518,8 ns 8226563,73          2
    countPrimeFactorStripe 30     742801122,9 ns 23886726,02          2
    countPrimeFactorStripe 31     728369885,4 ns 20177990,80          2
    countPrimeFactorStripe 32     766647220,8 ns 13231341,84          2



#### Answer:

> See code: `BenchmarkHistogram.java` for implementation.
> Our implementation of Histogram3 shows significant performance improvement compared to Histogram2 that does not use lock striping. This trend becomes more pronounced as the number of threads increases, showing that the lock-striping does indeed prevent saturation loss from multiple threads blocking each other, by trying to carry out similar tasks (updating the histogram).
> With our amount of threads locked to 10 and increasing the number of locks on the histogram for each iteration, we see that we gain a maximum performance after around 5 locks with no improvement after this point. Already at 5 locks, there is a relatively high chance that two threads are not trying to increment the same bucket at the same time. Therefore we see diminishing to no returns after this point.

