// // For week 7
// // raup@itu.dk * 10/10/2021

// package exercises07;

// // The solution is made with help from TAs to start out. 

import java.util.concurrent.atomic.AtomicReference;

class ReadWriteCASLock implements SimpleRWTryLockInterface {
private final AtomicReference<Holders> holder = new AtomicReference<Holders>();

    public boolean readerTryLock() {
		  Thread current = Thread.currentThread();
      Holders h = holder.get();
      while (h == null || h instanceof ReaderList) {
        ReaderList newRL = new ReaderList(current, (ReaderList)h);
        if (holder.compareAndSet(h, newRL)) return true;
        else h = holder.get(); // if failling look up h again. 
      }
      return false; // If a writer is in the holder.
    }
    
    public void readerUnlock() {
		// TODO 7.2.4
    }
    
    public boolean writerTryLock() {
		Thread current = Thread.currentThread();
		return holder.compareAndSet(null, new Writer(current));
    }

    public void writerUnlock() {
      Thread currentT = Thread.currentThread();
      Holders h = holder.get(); 
      if (h instanceof Writer && (Writer)h.thread == currentT){holder.compareAndSet(h, null);}
    }

    // Challenging 7.2.7: You may add new methods




    private static abstract class Holders { }

    private static class ReaderList extends Holders {
		private final Thread thread;
		private final ReaderList next;

        public ReaderList(Thread thread, ReaderList rest){
            this.thread = thread; 
            this.next = rest;
        }
		
        public boolean contains(Thread t){
            if (this.thread == t){return true;}
            else if (this.next == null) {return false;}
            return next.contains(t);
        }

        public ReaderList remove(Thread t){
            if(this.thread == t){  
                ReaderList newList = next;
                this.next = null; return newList;}
            else if (this.next == null){
                throw new Exception(); // evt returnere this
            } return new ReaderList(this.thread, next.remove(t));
        }
    }

    private static class Writer extends Holders {
		public final Thread thread;

		public Writer (Thread t){
            this.thread = t;
        }
    }
}
