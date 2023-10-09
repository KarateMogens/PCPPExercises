package exercises07;

import java.util.concurrent.atomic.AtomicInteger;

public class CasHistogram implements Histogram {

    private final AtomicInteger[] counts;

    public static void main(String[] args) {
        
    }

    public CasHistogram(int span){
        this.counts = new AtomicInteger[span];
        for (int i = 0; i < span; i++) {
            counts[i] = new AtomicInteger();
        }
    }

    @Override
    public void increment(int bin) {
        int oldvalue;
        int newvalue;
        do {
            oldvalue = counts[bin].get();
            newvalue = oldvalue + 1;
        } while (!counts[bin].compareAndSet(oldvalue, newvalue)); //Loops while the update to the AtomicInt is not carried out.
    }

    @Override
    public int getCount(int bin) {
        return counts[bin].get();
    }

    @Override
    public int getSpan() {
        return counts.length;
    }

    @Override
    public int getAndClear(int bin) {
        int oldvalue;
        int newvalue;
        do {
            oldvalue = counts[bin].get();
            newvalue = 0;
        } while (!counts[bin].compareAndSet(oldvalue, newvalue));
        return oldvalue;
    }
}