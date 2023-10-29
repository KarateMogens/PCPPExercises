package exercises07;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
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
import java.util.Collections;

// Concurrency imports
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestLocks {
    // The imports above are just for convenience, feel free add or remove imports
    
    private ReadWriteCASLock monitor;
    private AtomicInteger ctr;
    private CyclicBarrier barrier;


    @BeforeEach
    public void setup() {
        monitor = new ReadWriteCASLock();
    }

    @Test
    public void readWriteExclusivity() {

        monitor.readerTryLock();
        assertFalse(monitor.writerTryLock());
        
    }

    @Test
    public void writeReadExclusivity() {

        monitor.writerTryLock();        
        assertFalse(monitor.readerTryLock());
    }

    @Test
    public void writeUnlockWithoutLock() {

        Assertions.assertThrows(RuntimeException.class, () -> {
            monitor.writerUnlock();
        });
        
    }

    @Test
    public void readUnlockWithoutLock() {

        Assertions.assertThrows(RuntimeException.class, () -> {
            monitor.readerUnlock();
        });
        
    }

    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4,5})
    public void concurrentWriterLocks(int thread) {

        ctr = new AtomicInteger();
        int noThreads = (int) Math.pow(2,thread);
        barrier = new CyclicBarrier(noThreads + 1);
        Thread[] allThreads = new Thread[noThreads];

        for (int i = 0; i < noThreads; i++) {
            allThreads[i] = new Thread(() -> {
                try {
                    barrier.await();
                    if (monitor.writerTryLock())
                        ctr.incrementAndGet();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
            allThreads[i].start();
        }

        try {
            barrier.await();
            for (int i = 0; i < noThreads; i++) {
                allThreads[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println(ctr.get());
        assertEquals(ctr.get(), 1);

    }
}



