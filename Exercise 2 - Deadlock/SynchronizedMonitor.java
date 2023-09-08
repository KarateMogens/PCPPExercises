package exercises02;

public class SynchronizedMonitor {

    private int readAcquires         = 0;
    private int readReleases    = 0;
    private boolean writer      = false;


    public void readLock() {

        synchronized(this) {
            try {
                while (writer) {
                    this.wait();
                }
                readAcquires++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void readUnlock() {

        synchronized(this) {
            readReleases++;
            if (readAcquires == readReleases ) 
                this.notifyAll();
        }
    }

    public void writeLock() {

        synchronized(this) {
            try {
                while (writer) {
                    this.wait();
                }
                writer = true;
                while (readAcquires != readReleases) {
                    this.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            
        }
    }

    public void writeUnlock() {

        synchronized(this) {
            writer = false;
            this.notifyAll();
        }
    }
    
}
