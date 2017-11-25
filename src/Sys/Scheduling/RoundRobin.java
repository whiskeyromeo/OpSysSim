package Sys.Scheduling;

import Sys.PCB;

import java.util.ArrayList;

/**
 * @project OS_Simulator
 */
public class RoundRobin extends Scheduler {
 
    private ArrayList<PCB> queue = new ArrayList<>();

    public RoundRobin() { }


    public PCB getNextFromQueue() {
        return getNextFromQueue(queue);
    }

    public ArrayList<PCB> getQueue() {
        return queue;
    }

    public int getQueueSize() { return queue.size(); };

    public void addToQueue(PCB process) {

        queue.add(process);

    }

    public void resetQueue() {
        queue.clear();
    }



}
