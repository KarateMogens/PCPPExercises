// // For week 7
// // raup@itu.dk * 10/10/2021
// // Got help from TA, otcl, to start out and establish the concepts. 
package exercises07;

import java.util.concurrent.atomic.AtomicReference;

class ReadWriteCASLock implements SimpleRWTryLockInterface {

  private final AtomicReference<Holders> holder = new AtomicReference<Holders>();

  public boolean readerTryLock() {

    Thread currentThread = Thread.currentThread();
    Holders oldList = holder.get();

    while (oldList instanceof ReaderList || oldList == null) {

      // Create a new ReaderList with the thread as the first element
      Holders newList = new ReaderList(currentThread, (ReaderList) oldList);
      if (holder.compareAndSet(null, newList) || holder.compareAndSet((ReaderList) oldList, newList)) {
        return true;
      }

      // Begin a new iteration if swap wasn't successful.
      oldList = holder.get();
    }

    // If a writer is currently the holder
    return false;

  }

  public void readerUnlock() {

    Thread currentThread = Thread.currentThread();
    Holders oldList = holder.get();

    while (oldList != null && oldList instanceof ReaderList) {

      ReaderList oldReaderList = (ReaderList) oldList;
      if (oldReaderList.contains(currentThread)) {
        // Remove the element and try and swap the ReaderList.
        ReaderList newList = oldReaderList.remove(currentThread);
        if (holder.compareAndSet(oldReaderList, newList)) {
          return;
        }
      } else {
        // Throw exception if the thread is not on the current ReaderList
        throw new RuntimeException("The current thread does not have a read-lock to release!");
      }

      // New iteration begins
      oldList = holder.get();
    }
    throw new RuntimeException("The current thread does not have a read-lock to release!");
  }

  public boolean writerTryLock() {
    Thread currentThread = Thread.currentThread();
    return holder.compareAndSet(null, new Writer(currentThread));
  }

  public void writerUnlock() {
    Holders writer = new Writer(Thread.currentThread());
    if (!holder.compareAndSet(writer, null)) {
      throw new RuntimeException("The current thread does not have a write-lock to release");
    }
  }

  private static abstract class Holders {
  }

  private static class ReaderList extends Holders {
    private final Thread thread;
    private final ReaderList next;

    public ReaderList(Thread thread, ReaderList rest) {
      this.thread = thread;
      this.next = rest;
    }

    public boolean contains(Thread thread) {

      if (this.thread == thread) {
        return true;
      } else if (this.next == null) {
        return false;
      }
      return next.contains(thread);
    }

    public ReaderList remove(Thread t) {
      if (this.thread == t) {
        ReaderList newList = next;
        return newList;
      } else if (this.next == null) {
        throw new RuntimeException();
      }
      return new ReaderList(this.thread, next.remove(t));
    }
  }

  private static class Writer extends Holders {
    public final Thread thread;

    public Writer(Thread t) {
      this.thread = t;
    }
  }
}
