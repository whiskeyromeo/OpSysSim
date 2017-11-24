package Sys.Scheduling;

import Sys.Dispatcher;
import Sys.Kernel;
import Sys.Memory.MemoryManager;
import Sys.PCB;
import Sys.ProcessState;

import java.util.ArrayList;


/**
 * @project OS_Simulator
 *  There should be only one Long Term Scheduler
 *  --> Therefore a Singleton
 *
 *  This scheduler should determine which processes
 *
 */
public class LongTerm extends Scheduler {

    private static ArrayList<PCB> waitingQueue = new ArrayList<>();

    private static LongTerm longTermScheduler;
    private MemoryManager memoryManager = MemoryManager.getInstance();
    private static MultiLevel processScheduler = MultiLevel.getInstance();


    protected LongTerm() { }

    public static synchronized LongTerm getInstance() {
        if(longTermScheduler == null) {
            longTermScheduler = new LongTerm();
        }
        return longTermScheduler;
    }

    // Pass off a waiting process to the Multi-level scheduling queue
    public void scheduleWaitingProcess() {
        for(PCB process : waitingQueue) {
            int memoryRequired = process.getMemRequired();
            if(memoryRequired <= memoryManager.getCurrentMemory()) {
                process.setArrivalTime(0);
                process.setCurrentState(ProcessState.STATE.READY);
                memoryManager.allocateMemory(memoryRequired);
                processScheduler.scheduleProcess(process);
                waitingQueue.remove(process);
                return;
            }
        }
        System.out.println("----COULD NOT ADD PROCESS TO READY QUEUE!--------");

    }

    public void addToWaitingQueue(PCB process) {
        process.setCurrentState(ProcessState.STATE.WAIT);
        process.setArrivalTime(Kernel.getSystemClock());
        waitingQueue.add(process);
    }

    public ArrayList<PCB> getWaitingQueue() {
        return waitingQueue;
    }

    public void resetWaitingQueue() {
        waitingQueue.clear();
    }

}
