package Sys;

/**
 * @project OS_Simulator
 */
public class Semaphore {
    private int numSignals = 0;
    private int boundLimit = 0;

    public Semaphore(int bound) {
        this.boundLimit = bound;
    }

    public synchronized void lock() throws InterruptedException {
        while(this.numSignals == this.boundLimit) wait();
        this.numSignals++;
        this.notify();
    }

    public synchronized void release() throws InterruptedException {
        while(this.numSignals == 0) wait();
        this.numSignals--;
        this.notify();
    }
}
