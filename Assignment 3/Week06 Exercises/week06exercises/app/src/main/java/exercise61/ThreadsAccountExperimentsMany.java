package exercise61;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class ThreadsAccountExperimentsMany {

  static final int N = 10;
  static final int NO_TRANSACTION = 5;
  static final int NO_THREADS = 10;
  static final Account[] accounts = new Account[N];
  static final Thread[] threads = new Thread[NO_THREADS];
  static Random rnd = new Random();

  public static void main(String[] args) {
    new ThreadsAccountExperimentsMany();
  }

  public ThreadsAccountExperimentsMany() {
    for (int i = 0; i < N; i++) {
      accounts[i] = new Account(i);
    }

    ExecutorService pool = new ForkJoinPool(); //Uses default amount of threads, that is the threads available from the system.

    Future<?>[] myFutures = new Future[NO_THREADS];
    for (int i = 0; i < NO_THREADS; i++) {
      myFutures[i] = pool.submit(() -> doNTransactions(NO_TRANSACTION)); // A new runnable that is a lambda containing our simple method. Java infers that it is runnable/callable. Could also be solved by creating a doNTransactions task that extends runnable.
    }

    for (int i = 0; i < NO_THREADS; i++) {
      try {
        myFutures[i].get();
      } catch (InterruptedException e) {
        System.out.println("At i = " + i + " I got error: " + e.getCause());
        System.exit(0);
      } catch (ExecutionException e) {
        System.out.println("At i = " + i + " I got error: " + e.getCause());
        System.exit(0);
      }
    }

    pool.shutdown();

    //Original, thread-based solution
    // for (int i = 0; i < N; i++) {
    //   accounts[i] = new Account(i);
    // }
    // for (int i = 0; i < NO_THREADS; i++) {
    //   try {
    //     (threads[i] = new Thread(() -> doNTransactions(NO_TRANSACTION))).start();
    //   } catch (Error ex) {
    //     System.out.println("At i = " + i + " I got error: " + ex);
    //     System.exit(0);
    //   }
    // }
    // for (int i = 0; i < NO_THREADS; i++) {
    //   try {
    //     threads[i].join();
    //   } catch (Exception dummy) {
    //   }
    //   ;
    // }
  

  }

  private static void doNTransactions(int noTransactions) {
    for (int i = 0; i < noTransactions; i++) {
      long amount = rnd.nextInt(5000) + 100;
      int source = rnd.nextInt(N);
      int target = (source + rnd.nextInt(N - 2) + 1) % N; // make sure target <> source
      doTransaction(new Transaction(amount, accounts[source], accounts[target]));
    }
  }

  private static void doTransaction(Transaction t) {
    System.out.println(t);
    t.transfer();
  }

  static class Transaction {
    final Account source, target;
    final long amount;

    Transaction(long amount, Account source, Account target) {
      this.amount = amount;
      this.source = source;
      this.target = target;
    }

    public void transfer() {
      Account min = accounts[Math.min(source.id, target.id)]; // Always same index as the .id
      Account max = accounts[Math.max(source.id, target.id)];
      synchronized (min) {
        synchronized (max) { // excludes target and source from participating in other ongoing transactions.
          source.withdraw(amount);
          try {
            Thread.sleep(50);
          } catch (Exception e) {
          }
          ; // Simulate transaction time
          target.deposit(amount);
        }
      }
    }

    public String toString() {
      return "Transfer " + amount + " from " + source.id + " to " + target.id;
    }
  }

  static class Account {
    // should have transaction history, owners, account-type, and 100 other real
    // things
    public final int id;
    private long balance = 0;

    Account(int id) {
      this.id = id;
    }

    public void deposit(long sum) {
      balance += sum;
    }

    public void withdraw(long sum) {
      balance -= sum;
    }

    public long getBalance() {
      return balance;
    }
  }

}
