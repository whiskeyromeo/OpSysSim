package Sys.Scheduling;

import Sys.PCB;
import Sys.ProcessState;

/**
 * @project OS_Simulator
 */
public class MultiLevel {
    // Group processes based on type/CPU time/ IO Access/ memory size/etc...
    // 'n' number of queues --> n number of groups
    // Each queue has an associated priority and individual scheduling algorithm
    // In order for queue to execute --> all queues of higher priority should
    // be empty

    public SJF sjfScheduler;
    public RoundRobin roundRobinScheduler;
    public FCFS fcfsScheduler;

    // use to create timeslice to prevent starvation
    boolean runBackground;

    // Set the time quantum to determine which scheduler should be called upon
    public final int SJF_QUANTUM = 10;      // If a process has a burst time of < 10 --> sjf
    public final int RR_QUANTUM = 80;       // If a process has a burst time of < 40 --> rrobin
                                            // Otherwise --> FCFS


    public MultiLevel () {
        sjfScheduler = new SJF();
        roundRobinScheduler = new RoundRobin();
        fcfsScheduler = new FCFS();
        runBackground = true;
    }

    /**
     * This implements Multilevel scheduling with priorities based on burst time
     * and whether a process is ioBound or not --> IO bound processes should either end up
     * in SJF or Round Robin queues
     * @param process
     * @throws IllegalArgumentException
     */
    public void scheduleProcess(PCB process) throws IllegalArgumentException {
        if(process.getCurrentState() != ProcessState.STATE.READY)
            throw new IllegalArgumentException("Process must be in READY state");

        int remainingBurst = process.getRemainingBurst();

        if(remainingBurst < SJF_QUANTUM) {
            sjfScheduler.addToQueue(process);
        } else if(remainingBurst < RR_QUANTUM || process.isIoBound()) {
            roundRobinScheduler.addToQueue(process);
        } else {
            fcfsScheduler.addToQueue(process);
        }
    }

    /**
     * Get the next process to execute --> Exhaust each queue in order
     * @return
     */
    public PCB getNextProcess() {
        if(sjfScheduler.getQueue().size() > 0 && !runBackground ) {
            return sjfScheduler.getNextFromQueue();
        }
        if(roundRobinScheduler.getQueue().size() > 0 && !runBackground ) {
            return roundRobinScheduler.getNextFromQueue();
        }
        if(fcfsScheduler.getQueue().size() > 0) {
            return fcfsScheduler.getNextFromQueue();
        }
        return null;
    }








}
