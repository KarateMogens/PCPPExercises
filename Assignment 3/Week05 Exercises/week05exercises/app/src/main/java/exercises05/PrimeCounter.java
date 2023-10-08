package exercises05;

public class PrimeCounter {
  private int count= 0;
  public synchronized void increment() {
    count= count + 1;
  }
  public synchronized int get() { 
    return count; 
  }
  public synchronized void add(int c) {
    // to be filled in
  }
  public synchronized void reset() {
    // to be filled in
  }
}
    

