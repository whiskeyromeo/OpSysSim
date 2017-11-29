package Sys;

import Sys.Memory.MemoryManager;
import Sys.Scheduling.IOScheduler;
import Sys.Scheduling.LongTerm;
import Sys.Scheduling.MultiLevel;

/**
 * @author Will Russell on 11/8/17
 * @project OS_Simulator
 */
public class Kernel {

    private CPU cpu; // Each Kernel should have at least one CPU
    private volatile static int processCounter = 1;     // Should be used to assign the pid for each process

    private MemoryManager memoryManager = MemoryManager.getInstance();
    private MultiLevel multiLevel = MultiLevel.getInstance();
    private LongTerm longTerm = LongTerm.getInstance();
    private IOScheduler ioScheduler = IOScheduler.getInstance();


    private static int systemClock;
    private boolean processesExist = true;

    private static Kernel kernel;

    protected Kernel() {
        this.systemClock = 0;
        this.cpu = new CPU(1);

    }

    public static Kernel getInstance() {
        if(kernel == null) {
            kernel = new Kernel();
        }
        return kernel;
    }


    int count = 0;
    public void execute() {

        checkIfNoProcessesInSystem();
        //System.out.println("processes exist : " + processesExist);
        if(!InterruptHandler.interruptSignalled) {

            longTerm.scheduleWaitingProcess();
            ioScheduler.reScheduleCompleteProcesses();
            cpu.run();
        } else {
            System.out.println("interrupt was signalled --> kernel");
        }

    }

    public void checkIfNoProcessesInSystem() {
        int ioSize = ioScheduler.getIOQueueSize();
        int newSize = longTerm.getWaitingQueueSize();
        int multiSize = multiLevel.getReadyCount();
        int runningSize = cpu.getRunningCount();
        if(ioSize == 0 && newSize == 0 && multiSize == 0 && runningSize == 0) {
            processesExist =  false;
        } else {
            processesExist = true;
        }

        if(!processesExist) {
            //System.out.format("iosize : %d, multSize == %d, runsize == %d, newSize = %d\n", ioSize, multiSize, runningSize, newSize);
            System.out.println("interrupt signalled");
            InterruptHandler.signalInterrupt();
        }
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

    //-------GETTERS----------
    public void advanceClock() { this.systemClock++; }
    public void resetClock() { this.systemClock = 0; }

    //-------SETTERS----------
    public int getSystemClock() {
        return this.systemClock;
    }

    public static int getStaticSystemClock() {
        return systemClock;
    }
}
