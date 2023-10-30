## Exercise 7.1

Implement a CasHistogram class in the style of week 6 with this interface:

        interface Histogram {
            void increment(int bin);
            int getCount(int bin);
            int getSpan();
            int getAndClear(int bin);
        }

The implementation must use AtomicInteger (instead of locks), and only use the methods compareAndSet
and get; no other methods provided in the class AtomicInteger are allowed.

The method getAndClear returns the current value in the bin and sets it to 0.

### Mandatory

1. Write a class CasHistogram implementing the above interface. In your implementation, ensure that: i)
class state does not escape, and ii) safe-publication. Explain why i) and ii) are guaranteed in your imple-
mentation, and report any immutable variables.

> #### Answer:
> See code: `CasHistogram.java`
>
> The class state does not escape, because the only shared state variable is `counts` is declared as `private` and therefore cannot be accessed directly and manipulated by any other classes. Furthermore, throughout the class methods, we only ever return the values stored in each AtomicInteger of `counts` rather than the AtomicIntegers themselves. Therefore these cannot be modified by other classes either.
> The class ensures safe publication, because the only class field `counts` is declared as `final`. It is therefore not published to any other classes until the construction is finalized and the object is flushed to shared memory.


2. Write a parallel functional correctness test for CasHistogram to check that it correctly stores the number
of primes in the range (0, 4999999); as you did in exercise 6.3.3 in week 6. You must use JUnit 5 and the
techniques we covered in week 4. The test must be executed with 2n threads for n ∈ {0, . . . , 4}. To assert
correctness, perform the same computation sequentially using the class Histogram1 from week 6. Your
test must check that each bin of the resulting CasHistogram (executed in parallel) equals the result of
the same bin in Histogram1 (executed sequentially).

Note: The method getAndClear was not part of the Histogram interface in week 6. Consequently,
you will need to implement a getAndClear method for Histogram1 so that you can implement the
new interface. This is just a technicality, since the method is not used in the test.

> #### Answer:
> See code: `TestHistograms.java`
>
> Run with `gradle cleanTest test --tests exercises07.TestHistograms`

3. Measure the overall time to run the program above for CasHistogram and the lock-based Histogram week 6, concretely, Histogram2. For this task you should not use JUnit 5, as it is does offer good support to measure performance. Instead you can use the code in the file TestCASLockHistogram.java. It contains boilerplate code to evaluate the performance of counting prime factors using two Histogram classes. To execute it, simply create two objects named histogramCAS and histogramLock containing your implementation of Histogram using CAS (CasHistogram) and your implementation of Histogram using a single lock from week 6 (Histogram2).

What implementation performs better? The (coarse) lock-based implementation or the CAS-based one?

Is this result you got expected? Explain why.

Note: Most likely, your implementation for Histogram2 from week 6 does not have a getAndClear
method for the same reason as we mentioned above. Simply implement a lock-based method for this exercise so that Histogram2 can implement the new version of the Histogram interface.


>#### Results:

    countPrimeFactors Lock   threads: 1         1388785166,7 ns 9208083,54          2
    countPrimeFactors Cas    threads: 1         1415929197,9 ns 4184668,56          2
    ---
    countPrimeFactors Lock   threads: 2          950088791,8 ns 1440124,78          2
    countPrimeFactors Cas    threads: 2          904218100,0 ns  907859,50          2
    ---
    countPrimeFactors Lock   threads: 3          807118587,6 ns 5094160,72          2
    countPrimeFactors Cas    threads: 3          655321579,2 ns 2112749,74          2
    ---
    countPrimeFactors Lock   threads: 4          824511545,8 ns 4022180,45          2
    countPrimeFactors Cas    threads: 4          511568808,4 ns 1975189,28          2
    ---
    countPrimeFactors Lock   threads: 5          846397106,3 ns 6800660,75          2
    countPrimeFactors Cas    threads: 5          462424402,2 ns 4234765,30          2
    ---
    countPrimeFactors Lock   threads: 6          862180275,1 ns 6615173,84          2
    countPrimeFactors Cas    threads: 6          439997485,4 ns 7410496,23          2
    ---
    countPrimeFactors Lock   threads: 7          861275102,2 ns 8134954,35          2
    countPrimeFactors Cas    threads: 7          424737322,9 ns 6022238,83          2
    ---
    countPrimeFactors Lock   threads: 8          865254037,4 ns 6762590,52          2
    countPrimeFactors Cas    threads: 8          412245293,8 ns 11798155,39          2
    ---
    countPrimeFactors Lock   threads: 9          864804410,4 ns 10120403,82          2
    countPrimeFactors Cas    threads: 9          416247762,6 ns 10204500,07          2
    ---
    countPrimeFactors Lock   threads: 10         863191125,1 ns 7420427,48          2
    countPrimeFactors Cas    threads: 10         416210410,4 ns 5240161,30          2
    ---
    countPrimeFactors Lock   threads: 11         868230858,3 ns 7604425,52          2
    countPrimeFactors Cas    threads: 11         427163854,2 ns 7056886,53          2
    ---
    countPrimeFactors Lock   threads: 12         885044554,2 ns 9855191,79          2
    countPrimeFactors Cas    threads: 12         435898233,3 ns 4756633,10          2
    ---
    countPrimeFactors Lock   threads: 13         889691104,3 ns 14789595,87          2
    countPrimeFactors Cas    threads: 13         432581270,9 ns 6423787,33          2
    ---
    countPrimeFactors Lock   threads: 14         894690866,7 ns 13070656,17          2
    countPrimeFactors Cas    threads: 14         428537785,5 ns 5706056,72          2
    ---
    countPrimeFactors Lock   threads: 15         918407112,6 ns 35147060,23          2
    countPrimeFactors Cas    threads: 15         435737475,0 ns 11736035,87          2
    ---
    countPrimeFactors Lock   threads: 16         925074589,6 ns 27487055,91          2
    countPrimeFactors Cas    threads: 16         441177306,3 ns 26252058,74          2

> #### Answer:
> See code: `TestHistograms.java`
> As expected, the CAS-based histogram performs a lot better than the lock-based Histogram2. The CAS-based version is about 50% than the lock-based at its peak of 5 threads. At this point both methods do not improve in performance.

>This is based on the fact that when using CAS, we are not blocking threads if the update is unsuccessful as a lock-based histogram does. Therefore, there is no extra (heavy) computation in parking and waking threads again when they can access the shared resource. This of course comes with the penalty of higher memory overhead in the CAS-based version, when compared to the lock-based.

## Exercise 7.2

Recall read-write locks, in the style of Java’s `java.util.concurrent.locks.ReentrantReadWriteLock`.
As we discussed, this type of lock can be held either by any number of readers, or by a single writer.
In this exercise you must implement a simple read-write lock class SimpleRWTryLock that is not reentrant and that does not block. It should implement the following interface:

    interface SimpleRWTryLockInterface {
        public boolean readerTryLock();
        public void readerUnlock();
        public boolean writerTryLock();
        public void writerUnlock();
    }

For convenience, we provide the skeleton of the class in `ReadWriteCASLock.java`.

Method `writerTryLock` is called by a thread that tries to obtain a write lock. It must succeed and return true if the lock is not already held by any thread, and return false if the lock is held by at least one reader or by a writer.

Method `writerUnlock` is called to release the write lock, and must throw an exception if the calling thread does not hold a write lock.

Method `readerTryLock` is called by a thread that tries to obtain a read lock. It must succeed and return true if the lock is held only by readers (or nobody), and return false if the lock is held by a writer.

Method `readerUnlock` is called to release a read lock, and must throw an exception if the calling thread does not hold a read lock.

The class can be implemented using `AtomicReference` and `compareAndSet(...)`, by maintaining a
single field holders which is an atomic reference of type Holders, an abstract class that has two concrete
subclasses:

    private static abstract class Holders { }
    
    private static class ReaderList extends Holders {
        private final Thread thread;
        private final ReaderList next;
        ...
    }

    private static class Writer extends Holders {
        public final Thread thread;
        ...
    }

The `ReaderList` class is used to represent an immutable linked list of the threads that hold read locks. 

The `Writer` class is used to represent a thread that holds the write lock. When holders is null the lock is unheld. (Representing the holders of read locks by a linked list is very inefficient, but simple and adequate for illustration. The real Java `ReentrantReadWriteLock` essential has a shared atomic integer count of the number of locks held, supplemented with a ThreadLocal integer for reentrancy of each thread and for checking that only lock holders unlock anything. But this would complicate the exercise. Incidentally, the design used here allows the read locks to be reentrant, since a thread can be in the reader list multiple times, but this is inefficient too).

### Mandatory

1. Implement the writerTryLock method. It must check that the lock is currently unheld and then atomi-
cally set holders to an appropriate Writer object.

>**Answer:**
>See code `ReadWriteCASLock.java`

2. Implement the writerUnlock method. It must check that the lock is currently held and that the holder is
the calling thread, and then release the lock by setting holders to null; or else throw an exception.

>**Answer:**
>See code `ReadWriteCASLock.java`

3. Implement the readerTryLock method. This is marginally more complicated because multiple other
threads may be (successfully) trying to lock at the same time, or may be unlocking read locks at the same
time. Hence you need to repeatedly read the holders field, and, as long as it is either null or a Read-
erList, attempt to update the field with an extended reader list, containing also the current thread.
(Although the SimpleRWTryLock is not intended to be reentrant, for the purposes of this exercise you
need not prevent a thread from taking the same lock more than once).

>**Answer:**
>See code `ReadWriteCASLock.java`

4. Implement the readerUnlock method. You should repeatedly read the holders field and, as long as
**`i)`** it is non-null and **`ii)`** refers to a ReaderList and **`iii)`** the calling thread is on the reader list, create a new  reader list where the thread has been removed, and try to atomically store that in the holders field; if this succeeds, it should return. If holders is null or does not refer to a ReaderList or the current thread is not on the reader list, then it must throw an exception.

>**Answer:**
>See code `ReadWriteCASLock.java`

For the readerUnlock method it is useful to implement a couple of auxiliary methods on the immutable
ReaderList:

    public boolean contains(Thread t) { ... }
    public ReaderList remove(Thread t) { ... }

> #### Answer:
>See code `ReadWriteCASLock.java`

5. Write simple sequential JUnit 5 correctness tests that demonstrate that your read-write lock works with a
single thread. Your test should check, at least, that:
• It is not possible to take a read lock while holding a write lock.
• It is not possible to take a write lock while holding a read lock.
• It is not possible to unlock a lock that you do not hold (both for read and write unlock).
You may write other tests to increase your confidence that your lock implementation is correct.

> #### Answer:
>See code `TestLocks.java`
>
>Run with `gradle cleanTest test --tests exercises07.TestLocks`

6. Finally, write a parallel functional correctness test that checks that two writers cannot acquire the lock at the same time. You must use JUnit 5 and the techniques we covered in week 4. Note that for this exercise
readers are irrelevant. Intuitively, the test should create two or more writer threads that acquire and release the lock. You should instrument the test to check whether there were 2 or more threads holding the lock at the same time. This check must be performed when all threads finished their execution. This test should be performed with enough threads so that race conditions may occur (if the lock has bugs).

> #### Answer:
>See code `TestLocks.java`
>
>Run with `gradle cleanTest test --tests exercises07.TestLocks`
