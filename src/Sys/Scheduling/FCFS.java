package Sys.Scheduling;

import Sys.PCB;
import java.util.ArrayList;


/**
 * @project OS_Simulator
 *
 * Instantiates the FCFS scheduler
 * TODO : FIX THIS --> BROKE IT GOOD
 */
public class FCFS extends Scheduler {

    private ArrayList<PCB> queue = new ArrayList<>();

    public FCFS() {}

//     Add a process to the queue


    public ArrayList<PCB> getQueue() {
        return queue;
    }

    public void addToQueue(PCB process) {
        process.setNextBurst(process.getRemainingBurst());
        this.queue.add(process);
    }

    public PCB getNextFromQueue() {
        return getNextFromQueue(queue);

    }


}
