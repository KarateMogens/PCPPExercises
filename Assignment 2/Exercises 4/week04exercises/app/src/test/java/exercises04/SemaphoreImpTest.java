package exercises04;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public class SemaphoreImpTest {

    private AtomicInteger count;
    private SemaphoreImp mySemaphore;
    private int C;
    private int noThreads;
    private CyclicBarrier startBarrier;

    @BeforeEach
    public void initializeTest() {
        C = 10;
        noThreads = 15;
        count = new AtomicInteger(0);
        mySemaphore = new SemaphoreImp(C);
        startBarrier = new CyclicBarrier(noThreads + 1);

    }

    @RepeatedTest(100)
    //@Test
    public void testRelease() {
        //Release a lock, without acquiring, triggering mySemaphore.state to go below 0
        mySemaphore.release();

        for (int i = 0; i < noThreads; i++) {
            new Thread( () -> {
                try {
                    startBarrier.await();
                    mySemaphore.acquire();
                    count.incrementAndGet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        try {
            startBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        assertTrue(count.get() == C);

    }

}
