package Sys.Scheduling;

import Sys.Kernel;
import Sys.PCB;
import Sys.ProcessState;

import java.util.ArrayList;

/**
 * @project OS_Simulator
 */
public class SJF {

    private volatile static ArrayList<PCB> queue = new ArrayList<>();

    private static SJF sjfScheduler;

    private SJF() {}

    public static SJF getInstance() {
        if(sjfScheduler == null) {
            sjfScheduler = new SJF();
        }
        return sjfScheduler;
    }

    /**
     * Get the next process from the queue
     * @return the next process to be executed
     */
    public PCB getNextFromQueue() {
        PCB next;
        if(queue.size() > 0) {
            next = queue.get(0);
            queue.remove(0);
            next.setCurrentState(ProcessState.STATE.RUN);
            return next;
        } else {
            return null;
        }
    }

    /**
     * TODO: May need to fix this --> methinks the logic is a bit fecked
     * Non-Preemptive SJF algorithm ...simpler this way
     * @param process
     */
    public void addToQueue(PCB process){
        int i;
        if(queue.size() == 0) {
            queue.add(process);
            return;
        }
        for(PCB p : queue) {
            if(p.getBurstAmount() > process.getBurstAmount()) {
               i = queue.indexOf(p);
               queue.set(i, process);
               return;
            }
        }
        // No shorter processes were found
        queue.add(process);

    }

    /**
     * This will preempt a process --> maybe should not be used here
     * but instead used in the interrupt handler? or CPU?
     * @param process
     */
    public void preemptProcess(PCB process) {
        // Should preempt the current process
        int burst = process.getBurstAmount();
        int arrivalTime = process.getArrivalTime();
        int currentTime = Kernel.getSystemClock();
        int timeRemaining = (burst - (currentTime - arrivalTime));
        process.setBurstAmount(timeRemaining);
        process.setCurrentState(ProcessState.STATE.READY);
    }


}
