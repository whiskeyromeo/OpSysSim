package Sys.Scheduling;

import Sys.Kernel;
import Sys.Memory.MemoryManager;
import Sys.PCB;
import Sys.ProcessState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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
                process.setMemAllocated(memoryRequired);
                processScheduler.scheduleProcess(process);
                waitingQueue.remove(process);
                return;
            }
        }
//        System.out.println("-------------------------------------------------");
//        System.out.println("----COULD NOT ADD PROCESS TO READY QUEUE!--------");
//        System.out.println("-------------------------------------------------");

    }

    public void addToWaitingQueue(PCB process) {
        process.setCurrentState(ProcessState.STATE.WAIT);
        process.setArrivalTime(Kernel.getSystemClock());
        waitingQueue.add(process);
    }

    public List<PCB> getWaitingQueue() {
        return waitingQueue.stream().collect(Collectors.toList());
    }

    public void resetWaitingQueue() {
        waitingQueue.clear();
    }

}
