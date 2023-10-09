package exercise63;

import java.util.concurrent.locks.ReentrantLock;

class Histogram3 implements Histogram {

    private final int[] counts;
    private final ReentrantLock[] locks;
    private final int noLocks;

    public Histogram3(int span, int noLocks) {
        this.counts = new int[span];
        this.locks = new ReentrantLock[noLocks];
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new ReentrantLock();
        }
        this.noLocks = noLocks;
    }

    public void increment(int bin) {
        // if (bin >= counts.length) {
        //    return; 
        // }
        int lock = bin % noLocks; 
        locks[lock].lock(); 
        counts[bin]++;
        locks[lock].unlock();
        }

    public int getCount(int bin) {
        // not sure how to lock on this, if needed
        return counts[bin];
    }

    public float getPercentage(int bin) {
        int total = 0;
        for (int i = 0; i < counts.length; i++) {   
            total += counts[i];
        }
        return counts[bin] / (float) total;
    }

    public int getSpan() {
        return counts.length;
    }
}
