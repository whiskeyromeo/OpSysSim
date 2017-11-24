package Sys;

import Sys.Memory.MemoryManager;
import Sys.Scheduling.MultiLevel;

/**
 * @author Will Russell on 11/8/17
 * @project OS_Simulator
 */
public class Kernel {

    private CPU cpu; // Each Kernel should have at least one CPU
    private static final int BASE_CORES = 1; // The number of cores to use in the system
    private volatile static int processCounter = 1;     // Should be used to assign the pid for each process
    private MemoryManager memoryManager;
    private MultiLevel scheduler;
    private static int systemClock;


    public Kernel() {
        this.cpu = new CPU(1); // TODO: starting with 1 cpu to test
        memoryManager = MemoryManager.getInstance();
        scheduler = scheduler.getInstance();
        systemClock = 0;
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

    public static void advanceClock() { systemClock++; }

    public static int getSystemClock() {
        return systemClock;
    }
}
