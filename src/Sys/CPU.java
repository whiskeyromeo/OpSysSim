package Sys;

/**
 * @author Capitan on 11/7/17
 * @project OS_Simulator
 */
public class CPU implements Runnable{

    private long clock;
    private int memory;
    private int cpuID;      // Figure we need an id to keep track of each CPU in case of multiple cores
    private PCB currentProcess;

    // May not be necessary...
    //private int modeBit; // 0 for kernel, 1 for user

    private static final int BASE_MEMORY = 4096;
    private static final int BASE_CORES = 1;



    private final InterruptHandler interruptHandler;


    public CPU() {
        this(BASE_MEMORY, BASE_CORES, 1);
    }

    public CPU(int memory, int numCores, int cpuId) {
        this.interruptHandler = new InterruptHandler();
        this.memory = memory;
        this.cpuID = cpuId;
    }

    public void setCurrentProcess(PCB currentProcess) {
        this.currentProcess = currentProcess;

        if(this.currentProcess == null) {
            // should call to the scheduler to get the next process

        }

    }

    private void cycle() {
        clock++; // increase the clock time each cycle
        // ioDevice cycle goes here?


    }

    /**
     * To work with the Runnable interface
     */
    public void run() {

    }


}
