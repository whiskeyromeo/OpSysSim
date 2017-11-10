package Sys;

/**
 * @author Will Russell on 11/8/17
 * @project OS_Simulator
 */
public class Kernel {

    private CPU cpu;          // Each Kernel should have at least one CPU

    private volatile static int processCounter = 1;     // Should be used to assign the pid for each process --> Maybe should be volatile to be threadsafe?
    private volatile static int systemClock = 0;  // needs to be static....but I do not think this the right way to do this

    public Kernel() {
        this.cpu = new CPU();
    }


    // ******** GETTERS *********
    public static int getSystemClock() { return systemClock; }


    //******* SETTERS ********
    public static void advanceClock() {
        systemClock++;
    }


    /**
     * Generates pids for new processes
     * @return pid of new process
     */
    public static int getNewPid() {
         int new_pid = processCounter;
         processCounter++;
         return new_pid;
    }



}
