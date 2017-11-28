package Sys.Scheduling;

import Sys.Memory.MemoryManager;
import Sys.PCB;
import Sys.ProcessState;
import User_space.CLI;

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
    private MultiLevel processScheduler = MultiLevel.getInstance();

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
                CLI.newProcessList.remove(process);
                CLI.activeProcessList.add(process);
                //process.setArrivalTime(kernel.getSystemClock());
                process.setCurrentState(ProcessState.STATE.READY);
                memoryManager.allocateMemory(memoryRequired);
                process.setMemAllocated(memoryRequired);
                processScheduler.scheduleProcess(process);
                waitingQueue.remove(process);
                return;
            }
        }
        // Not enough memory...do something here

    }

    public void addToWaitingQueue(PCB process) {
        CLI.newProcessList.add(process);
        process.setCurrentState(ProcessState.STATE.WAIT);
        //process.setArrivalTime(kernel.getSystemClock());
        waitingQueue.add(process);
    }

    public int getWaitingQueueSize() { return waitingQueue.size(); }

    public static ArrayList<PCB> getWaitingQueue() {
        return waitingQueue;
    }

    public List<PCB> streamWaitingQueue() {
        return waitingQueue.stream().collect(Collectors.toList());
    }

    public void resetWaitingQueue() {
        waitingQueue.clear();
    }

}
