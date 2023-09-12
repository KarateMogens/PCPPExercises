package exercise3;

import java.util.concurrent.CyclicBarrier;

public class BarrierTest {

    private int field = 0;
    CyclicBarrier br = new CyclicBarrier(1);

    public void incrementField() {
        try {
            br.await();
            Thread.sleep(5);
            field++;
            System.out.println("Incremented value to :" + field);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getField() {
        try {
            br.await();
            return field;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) {
        BarrierTest myTest = new BarrierTest();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                myTest.incrementField();
            }
        });


        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                myTest.incrementField();
            }
        });

        t1.start(); t2.start();
    
        try {
            t1.join();
            t2.join();

        } catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("Final count is: " + myTest.getField());
    }
    
}
