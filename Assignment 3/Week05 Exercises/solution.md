### Exercise 5.1

In this exercise you must perform, on your own hardware, the measurement performed in the lecture using the example code in file TestTimeThreads.java.


1. First compile and run the thread timing code as is, using Mark6, to get a feeling for the variation and robustness of the results. Do not hand in the results but discuss any strangenesses, such as large variation in the time measurements for each case.

Reminder:

    Message         Mean run-time    std. deviation     Iterations (10*i)
    hashCode()      500,0 ns         893,21              2
    hashCode()      188,5 ns         58,55               4
    hashCode()      161,0 ns         24,72               8
    hashCode()      162,5 ns         63,26               16
    hashCode()      114,1 ns         36,37               32

#### Answer:

> The result of running Mark6 on the TestTimeThreads, shows an expected result for the different tasks, with evenly distributed measurements as the no of tasks measured is increased. There are some spikes in some of the measurements. Those are very obvious in the measurements for `hashcode()`. They can be explained by disturbances from other tasks on the CPU.

> In general, we see that the measurements converge to a stable value as the number of iterations increases. This makes a lot of sense, because outlier measurements caused by other CPU-tasks have much less of an impact on the mean, as the number of iterations increases.
>
> We can also see this reflected in the fact that the standard deviation converges towards a lower value, as the number of iterations increase.


2. Now change all the measurements to use Mark7, which reports only the final result. Record the results in a text file along with appropriate system identification.

Include the results in your hand-in, and reflect and comment on them: Are they plausible? 

Any surprises? 

Mention any cases where they deviate significantly from those shown in the lecture.

   

#### Results:

    # OS:   Mac OS X; 13.5.1; aarch64
    # JVM:  Amazon.com Inc.; 19.0.2
    # CPU:  null; 8 "cores"
    # Date: 2023-09-25T16:46:54+0200
    Mark 7 measurements
    hashCode()                            3,5 ns       0,12  134217728
    Point creation                       55,6 ns       1,22    8388608
    Thread's work                      7069,4 ns     263,00      65536
    Thread create                       539,7 ns      21,83     524288
    Thread create start               28877,2 ns    3475,34      16384
    Thread create start join          42736,0 ns    1299,55       8192
    ai value = 1802180000
    Uncontended lock                      8,8 ns       0,02   33554432

#### Answer:

> Our computations also verify the results that were shown during the lecture. In general, our results were consistent with or a little bit faster than the results from Jørgen's measurements. `Thread create` and `Thread create start` are both a little faster, which makes sense considering we are using newer hardware.
>
> We have a somewhat high standard deviation on the method `Thread create start`. Since this is a heavier operation, we are also measuring a mean on much fewer iterations. Therefore, the standard deviation has not yet converged as much as the other operations.

---


### Exercise 5.2 

In this exercise you must use the benchmarking infrastructure to measure the performance of the prime counting example given in file `TestCountPrimesThreads.java`.

1. Measure the performance of the prime counting example on your own hardware as a function of the number of threads used to determine whether a given number is a prime. Record system information as well as the measurement results for 1. . . 32 threads in a text file. If the measurements take excessively long time on your computer, you may measure just for 1. . . 16 threads instead.

#### Answer
    # OS:   Mac OS X; 13.5.1; aarch64
    # JVM:  Amazon.com Inc.; 19.0.2
    # CPU:  null; 8 "cores"
    # Date: 2023-09-25T17:26:00+0200
    countSequential                 2599071,2 ns   11368,25        128
    countParallelN       1          2662439,5 ns    5120,03        128
    countParallelN       2          1763260,8 ns    9529,42        256
    countParallelN       3          1435813,2 ns   23490,30        256
    countParallelN       4          1324613,3 ns   34529,28        256
    countParallelN       5          1605239,7 ns   28767,50        256
    countParallelN       6          1664559,6 ns   28849,69        256
    countParallelN       7          1713249,3 ns   49087,01        256
    countParallelN       8          1728534,7 ns   28561,28        256
    countParallelN       9          1746156,6 ns   28324,64        256
    countParallelN      10          1762656,3 ns   29360,12        256
    countParallelN      11          1772139,2 ns   32144,75        256
    countParallelN      12          1782867,1 ns   37717,26        256
    countParallelN      13          1791919,0 ns   26801,21        256
    countParallelN      14          1807322,1 ns   31872,05        256
    countParallelN      15          1981854,0 ns  165059,06        256
    countParallelN      16          1841891,9 ns   41298,14        256
    countParallelN      17          1864650,4 ns   30727,48        256
    countParallelN      18          1877349,1 ns   31855,49        256
    countParallelN      19          1853504,1 ns   31922,21        256
    countParallelN      20          1873112,4 ns   40791,61        256
    countParallelN      21          1871664,0 ns   27801,22        256
    countParallelN      22          1880877,7 ns   44097,57        256
    countParallelN      23          1859708,9 ns   25386,30        256
    countParallelN      24          1871998,8 ns   32677,27        256
    countParallelN      25          1876877,8 ns   28796,54        256
    countParallelN      26          1883915,3 ns   31105,25        256
    countParallelN      27          1865180,8 ns   27929,33        256
    countParallelN      28          1866093,6 ns   25772,04        256
    countParallelN      29          1862815,1 ns   30380,10        256
    countParallelN      30          1845232,6 ns   39212,44        256
    countParallelN      31          1894124,4 ns  132886,79        256
    countParallelN      32          1821142,0 ns   38100,05        256

2. Reflect and comment on the results; are they plausible? Is there any reasonable relation between the number of threads that gave best performance, and the number of cores in the computer you ran the benchmarks on? Any surprises?

#### Answer:

> Making the time-measurement of the Prime computation. There is a clear minimum compute time at the point of 4 threads being utilized. All measurements above this point, show values between 1,600,000-1,800,000 ns.
>
>Considering the architecture of the CPU in use, this actually makes a lot of sense. Even though the CPU has 8 cores in total, all cores are not even. 4 cores are performance cores, while the other 4 are efficiency cores (that do not have the same performance).
>
>Therefore at the point of using 5 cores, an efficiency core can essentially "block" a piece of work from being done by a performance core.
>
>Furthermore, the last chunk of work is much more computationally heavy than the first part. When one of the last chunks of work is assigned to a effeciency core, we can expect the whole task is just waiting for this to finish.
>




---


### Exercise 5.3 


In this exercise you should estimate whether there is a performance gain by declaring a shared variable as `volatile`. Consider this simple class that has both a volatile int and another int that is not declared volatile:

    public class TestVolatile {

        private volatile int vCtr;
        private int ctr;

        public void vInc() {
            vCtr++; }
        public void inc() {
            ctr++;
        } 
    }

Use Mark7 (from `Benchmark.java`) to compare the performance of incrementing a volatile int and a normal int. Include the results in your hand-in and comment on them: Are they plausible? Any surprises?

#### Results:

    # OS:   Mac OS X; 14.0; aarch64
    # JVM:  Eclipse Adoptium; 11.0.17
    # CPU:  null; 8 "cores"
    # Date: 2023-10-06T14:05:37+0200
    Volatile           6,6 ns       0,03   67108864
    Normal             0,9 ns       0,01  268435456


#### Answer:

>It is expected that it will be much slower to increment a volatile integer, since the value of the variable `vCtr` has to be flushed to the shared cache/memory after each increment, whereas the value of the non-volatile counter `ctr` can be stored in a low-level cache between increments. 


---

### Exercise 5.4

In this exercise you must write code searching for a string in a (large) text. Such a search is the core of any web-crawling service such as Google, Bing, Duck-Go-Go etc. Later in the semester, there will be a guest lecture from a Danish company providing a very specialized web-crawling solution that provides search results in real-time.

In this exercise you will work with the nonsense text found in: src/main/resources/long-text-file.txt (together with the other exercise code). You may read the file with this code:

    final String filename = "src/main/resources/long-text-file.txt";
    ...
    public static String[] readWords(String filename) {
    try {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        return reader.lines().toArray(String[]::new); //will be explained in Week 7
    } catch (IOException exn) { return null;}
    }
`readWords()` will give you an array of lines, each of which is a string of (nonsense) words.

The purpose of the code you are asked to write is to find all occurences of a particular word in the text. This skeleton is based on sequentially searching the text (i.e. one thread). You may find it in the code-exercises directory for Week05.

    public class TestTimeSearch {
    public static void main(String[] args) { new TestTimeSearch(); }
    public TestTimeSearch() {
        final String filename = "src/main/resources/long-text-file.txt";
        final String target= "ipsum";
        final LongCounter lc= new LongCounter();
        String[] lineArray= readWords(filename);
        System.out.println("Array Size: "+ lineArray.length);
        System.out.println("# Occurences of "+target+ " :"
            +search(target, lineArray, 0, lineArray.length, lc));
    }
    static long search(String x, String[] lineArray, int from,
                                            int to, LongCounter lc){
        //Search each line of file
        for (int i=from; i<to; i++ ) lc.add(linearSearch(x, lineArray[i]));
        return lc.get();
    }
    static long linearSearch(String x, String line) {
        //Search for occurences of c in line
        String[] arr= line.split(" ");
        long count= 0;
        for (int i=0; i<arr.length; i++ ) if ( (arr[i].equals(x)) ) count++;
        return count;
    } }

1. `TestTimeSearch` uses a slightly extended version of the `LongCounter` where two methods have been added `void add(long c)` that increments the counter by c and `void reset()` that sets the counter to 0. Extend LongCounter with these two methods in such a way that the counter can still be shared safely by several thread:

#### Answer:

> See code in `LongCounter.java`


2. How many occurencies of "ipsum" is there in `long-text-file.txt`. Record the number in your solution.

>Array Size: 5697
>Occurences of ipsum :1430

3. Use Mark7 to benchmark the search function. Record the result in your solution.


#### Results:
    TestTimeSearch (Linear)         6113708,1 ns   29466,65         64

#### Answer:

    public TestTimeSearch(int j) {
        final String filename = "src/main/resources/long-text-file.txt";
        final String target= "ipsum";

        final LongCounter lc = new LongCounter();  //name is abit misleading, it is just a counter
        String[] lineArray= readWords(filename);
        Benchmark.Mark7("TestTimeSearch (Linear)", i -> search(target, lineArray, 0, lineArray.length, lc));
    }


4. Extend the code in TestTimeSearch with a new method

        private static long countParallelN(String target,
                          String[] lineArray, int N, LongCounter lc) {
        // uses N threads to search lineArray
          ...
        }

Fill in the body of countParallelN in such a way that the method uses N threads to search the lineArray.

Provide a few test results that makes it plausible that your code works correctly.

#### Answer:

    private static long countParallelN(String target, String[] lineArray, int noOfThreads, LongCounter lc) {
        
        //lc.reset();

        int range = lineArray.length;

        int rangeSize = range / noOfThreads;
        Thread[] threads = new Thread[noOfThreads];
        for (int i = 0; i < noOfThreads; i++) {
        int from = i*rangeSize;
        int to = (i + 1 == noOfThreads) ? range : rangeSize*(i+1); //Checks if we've reached the last range
        threads[i] = new Thread( () -> {
            for (int j = from; j < to; j++ ) {
            lc.add(linearSearch(target, lineArray[j]));
            }
        });
        threads[i].start();      
        }
        try {
        for (int i = 0; i < noOfThreads; i++) {
            threads[i].join();
        }
        } catch (InterruptedException e) {
        e.printStackTrace();
        } 
        return lc.get();
    }

#### Test results:
    Occurences of ipsum in linear search:1430
    Occurences of ipsum in parallel search:1430
    Occurences of libero in linear search:981
    Occurences of libero in parallel search:981
    Occurences of lorem in linear search:1147
    Occurences of lorem in parallel search:1147

5. Use Mark7 to benchmark countParallelN. Record the result in your solution and provide a small discussion of the timing results.

#### Results:

    TestTimeSearch (Linear)           6392083,8 ns   22739,02         64
    TestTimeSearch (Threads: 4)       2012947,7 ns   76571,93        128

#### Answer:

> The method `countParallelN()` executes roughly 3 times faster when running on 4 cores (which we can expect to be the optimal number of threads to run on this specific CPU). Both standard deviations are very acceptable.
>
>It makes sense that the parallelized version of the search is much faster than the sequential (linear) search of the text, because the same amount of work is shared between multiple threads.