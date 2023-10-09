package exercise63;

class Histogram2 implements Histogram {

    private final int[] counts;
    private volatile int total; //Should it be volatile or not?

    public Histogram2(int span) {
        this.counts = new int[span];
    }

    public synchronized void increment(int bin) {
        // if (bin >= counts.length) {
        //     return;
        // }
        counts[bin] = counts[bin] + 1;
        total++;
        
    }

    public synchronized float getPercentage(int bin) {
        return counts[bin] / (float) total;
    }

    public synchronized int getCount(int bin) {
        return counts[bin];
    }

    public int getSpan() {
        return counts.length;
    }
}
