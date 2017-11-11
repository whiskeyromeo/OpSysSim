package Sys.Scheduling;

import Sys.PCB;
import java.util.ArrayList;


/**
 * @project OS_Simulator
 */
public class FCFS {

    private volatile static ArrayList<PCB> queue = new ArrayList<>();

    private static FCFS fcfsScheduler;
    private FCFS() {}

    public static FCFS getInstance() {
        if(fcfsScheduler == null) {
            fcfsScheduler = new FCFS();
        }
        return fcfsScheduler;
    }


    // Just in case we need to get the whole queue
    public static ArrayList<PCB> getQueue() {
        return queue;
    }

    // Add a process to the queue
    public static void addToQueue(PCB process) {
        queue.add(process);
    }

    // Should return null if no other processes in queue
    public static PCB getNextFromQueue() {
        PCB next;
        if(queue.size() > 0) {
            next = queue.get(0);
            queue.remove(0);
            return next;
        } else {
            return null;
        }
    }
}
