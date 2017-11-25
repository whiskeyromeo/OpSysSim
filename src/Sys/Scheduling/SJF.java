package Sys.Scheduling;

import Sys.Kernel;
import Sys.PCB;
import Sys.ProcessState;

import java.util.ArrayList;

/**
 * @project OS_Simulator
 *
 *  Instantiates the SJF scheduler as a singleton
 *
 */
public class SJF extends Scheduler{

    private  ArrayList<PCB> queue = new ArrayList<>();

    public SJF() {}

    /**
     * Non-Preemptive SJF algorithm ...simpler this way
     * @param process
     */

    public void addToQueue(PCB process){
        int i;
        // Set the nest burst here to remain consistent across schedulers
//        process.setBurstTime(process.getRemainingBurst());

        if(queue.size() == 0) {
            queue.add(process);
            return;
        }
        for(PCB p : queue) {
            if(p.getNextBurst() > process.getNextBurst()) {
               i = queue.indexOf(p);
               queue.set(i, process);
               return;
            }
        }
        // No shorter processes were found
        queue.add(process);

    }

    public PCB getNextFromQueue() {
        return getNextFromQueue(queue);
    }

    public ArrayList<PCB> getQueue() {
        return queue;
    }

    public void resetQueue() {
        queue.clear();
    }



    /**
     * This will preempt a process --> maybe should not be used here
     * but instead used in the interrupt handler? or CPU?
     * @param process
     */
//    public void preemptProcess(PCB process) {
//        // Should preempt the current process
//        int burst = process.getRemainingBurst();
//        int arrivalTime = process.getArrivalTime();
//        int currentTime = Kernel.getSystemClock();
//        int timeRemaining = (burst - (currentTime - arrivalTime));
//        process.setRemainingBurst(timeRemaining);
//        process.setNextBurst(timeRemaining);
//        process.setCurrentState(ProcessState.STATE.READY);
//    }


}
