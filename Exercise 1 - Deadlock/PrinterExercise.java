package exercises01;

import java.util.concurrent.locks.ReentrantLock;

public class PrinterExercise {

  ReentrantLock lock = new ReentrantLock();

  
  public void print() {
      lock.lock();
      System.out.print("-");
      try {
          Thread.sleep(50);
      } catch (InterruptedException exn) {}
      System.out.print("|");
      lock.unlock();
    }



  public static void main(String[] args) {
    PrinterExercise printer = new PrinterExercise();
    Thread t1 = new Thread(() -> {
        while (true) 
          printer.print();
        }
      );
    Thread t2 = new Thread(() -> {
        while (true) 
          printer.print();
        }
      );
    t1.start();
    t2.start();
  }  
}
  
