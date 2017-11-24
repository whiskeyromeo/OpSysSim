package Sys.Scheduling;

import Sys.PCB;

import java.util.ArrayList;

/**
 * @project OS_Simulator
 */
public class RoundRobin extends Scheduler {
    private final int TIME_QUANTUM = 20;
    private ArrayList<PCB> queue = new ArrayList<>();

    public RoundRobin() { }


    public PCB getNextFromQueue() {
        return getNextFromQueue(queue);
    }

    public ArrayList<PCB> getQueue() {
        return queue;
    }

    public void addToQueue(PCB process) {
        // Will only be scheduled to Round Robin again if
        // remaining time is between 10 and the upper bound set
        // in MultiLevel
        process.setBurstTime(TIME_QUANTUM);

        //The Multilevel scheduler will ensure that only
        // processes with remaining burst times between 10 and 40 cycles
        // are placed in this queue
        queue.add(process);

    }

    public void resetQueue() {
        queue.clear();
    }



}
