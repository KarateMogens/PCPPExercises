package exercises04;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
// TODO: Very likely you need to expand the list of imports
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;

public class ConcurrentSetTest {

    // Variable with set under test
    private ConcurrentIntegerSet set;
    private CyclicBarrier myBarrier;
    private int threads = 16;
    private int setSize = 1000;

    // TODO: Very likely you should add more variables here
        

    // Uncomment the appropriate line below to choose the class to
    // test
    // Remember that @BeforeEach is executed before each test
    @BeforeEach
    public void initialize() {
		// init set
    myBarrier = new CyclicBarrier(threads + 1);
		// set = new ConcurrentIntegerSetBuggy();
		// set = new ConcurrentIntegerSetSync();	
		set = new ConcurrentIntegerSetLibrary();
    }

    @RepeatedTest(5000)
    @Disabled
    public void testAddConcurrentIntegerSet() {
    
      for (int i = 0; i < threads; i++) {
        new Thread(() -> {
            try {
              myBarrier.await();
              for (int j = 0; j < setSize; j++) {
                set.add(j);
              }
              myBarrier.await();
            } catch (InterruptedException e) {
              e.printStackTrace();
            } catch (BrokenBarrierException e) {
              e.printStackTrace();
            } 
          }
        ).start();
      }
      try {
        myBarrier.await();
        myBarrier.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (BrokenBarrierException e) {
        e.printStackTrace();
      }

      assertEquals(setSize, set.size());
      //assertTrue(setSize == set.size());
    }

    @RepeatedTest(5000)
    //@Disabled
    public void testRemoveConcurrentIntegerSet() {
      for (int i = 0; i < setSize; i++) {
        set.add(i);
      }
      for (int i = 0; i < threads; i++) {
        new Thread(() -> {
            try {
              myBarrier.await();
              for (int j = 0; j < setSize; j++) {
                set.remove(j);
              }
              myBarrier.await();
            } catch (InterruptedException e) {
              e.printStackTrace();
            } catch (BrokenBarrierException e) {
              e.printStackTrace();
            } 
          }
        ).start();
      }
      try {
        myBarrier.await();
        myBarrier.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (BrokenBarrierException e) {
        e.printStackTrace();
      }
      assertEquals(0, set.size());
      //assertTrue(0 == set.size());
    }

}
