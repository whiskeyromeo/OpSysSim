package Sys.Scheduling;

import Sys.PCB;

import java.util.ArrayList;

/**
 * TODO: TOSS THIS AFTER HARVESTING WHAT IS WANTED --> THIS IS MOSTLY JUNK
 *
 *
 * @project OS_Simulator
 */
public abstract class Scheduler {

    private ArrayList<PCB> queue = new ArrayList<>();


    public class Stats {
        private int turnaround;
        private int waiting;
        private int response;
        private int processCount;

        public double getAvgTurnaround() {
            return turnaround/(double) processCount;
        }

        public double getAvgWaiting() {
            return waiting/(double) turnaround;
        }

        public double getAvgResponse() {
            return response/(double) processCount;
        }

        public void addProcess() {
            processCount++;
        }

    }

    public PCB getNextFromQueue() {
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
