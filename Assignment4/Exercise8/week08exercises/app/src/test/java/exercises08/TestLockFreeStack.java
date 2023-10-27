// raup@itu.dk * 2023-10-20 
package exercises08;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
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

class TestLockFreeStack {

    LockFreeStack<Integer> concurrentStack;
    CyclicBarrier myBarrier;
    AtomicInteger myAtomicInteger;
    final int integerRange = 200;

    @BeforeEach
    public void initializeTest() {
        concurrentStack = new LockFreeStack<Integer>();
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3, 4, 5, })
    public void pushConcurrentTest(int threads) {

        int noThreads = (int) Math.pow(2, threads);
        int perThread = integerRange / noThreads;
        myBarrier = new CyclicBarrier(noThreads);

        //Create and start threads, each adding a range of integers to the stack.
        Thread[] myThreads = new Thread[noThreads];
        for (int i = 0; i < noThreads; i++) {
            int from = perThread * i;
            int to = (i + 1 == noThreads) ? integerRange : perThread * (i + 1);
            myThreads[i] = new Thread(() -> {
                try {
                    myBarrier.await();
                    for (int j = from; j < to; j++) {
                        concurrentStack.push(j);
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
            myThreads[i].start();
        }

        // Join all threads - thereby ensuring that their execution has finished.
        for (Thread thread : myThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int result = 0;
        for (int i = 0; i < integerRange; i++) {
            result += concurrentStack.pop();
        }

        // Calculates the sum from the triangular sum given by 1+2+3+...+n = (n^2 + n)/2
        int sum = ((int) Math.pow(integerRange-1, 2) + integerRange-1)/2;

        assertEquals(sum, result);
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3, 4, 5, })
    public void popConcurrentTest(int threads) {

        myAtomicInteger = new AtomicInteger();

        // Push all elements from 0 to integerRange onto the stack, and simoultaneously calculating the sum.
        int sum = 0;
        for (int i = 0; i < integerRange; i++) {
            concurrentStack.push((i));
            sum += i;
        }

        int noThreads = (int) Math.pow(2, threads);
        int perThread = integerRange / noThreads;
        myBarrier = new CyclicBarrier(noThreads);

        //Create and start threads, each thread popping itegerRanger/noThreads many items and adding them to an AtomicInteger
        Thread[] myThreads = new Thread[noThreads];
        for (int i = 0; i < noThreads; i++) {
            int from = perThread * i;
            int to = (i + 1 == noThreads) ? integerRange : perThread * (i + 1);
            myThreads[i] = new Thread(() -> {
                try {
                    myBarrier.await();
                    for (int j = from; j < to; j++) {
                        myAtomicInteger.addAndGet(concurrentStack.pop());
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
            myThreads[i].start();
        }

        //Join all threads, making sure their execution is finished.
        for (Thread thread : myThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        assertEquals(sum, myAtomicInteger.get());
    }
    

}
