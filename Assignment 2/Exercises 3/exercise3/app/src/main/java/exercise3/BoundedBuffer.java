import java.util.LinkedList;
import java.util.concurrent.Semaphore;


public class BoundedBuffer<T> implements BoundedBufferInteface<T> {

    private Semaphore semTake;
    private Semaphore semInsert;
    private Semaphore pseudoLock;

    private LinkedList<T> myList;

    public BoundedBuffer(int cap) throws Exception {
        semTake = new Semaphore(cap, true);
        semInsert = new Semaphore(cap, true);
        pseudoLock = new Semaphore(1, true);
        myList = new LinkedList<>();

        for (int i = 0; i < cap; i++) {
            semTake.acquire();
        }
    }

    public T take() throws Exception {
        semTake.acquire();

        //Ensuring mutual exclusivity - critical section
        pseudoLock.acquire();
        T elementToRemove = myList.removeLast();
        System.out.println("removed element " + elementToRemove);
        pseudoLock.release();

        semInsert.release();
        return elementToRemove;
    }

    public void insert(T elem) throws Exception {
            semInsert.acquire();

            //Ensuring mutual exclusivity - critical section
            pseudoLock.acquire();
            myList.addFirst(elem);
            System.out.println("inserted element " + elem);
            pseudoLock.release();

            semTake.release();
    }

    public static void main(String[] args) {
        try {
            BoundedBuffer<Integer> bb = new BoundedBuffer<>(10);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    bb.insert(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                bb.take();
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        producer.start();
        consumer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}