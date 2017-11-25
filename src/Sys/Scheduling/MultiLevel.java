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
    public final int SJF_QUANTUM = 30;      // If a process has a burst time of < 10 --> sjf
    public final int RR_SCHED_QUANTUM = 250;       // If a process has a burst time of < 40 --> rrobin
    public final int RR_TIME_QUANTUM = 30;
                                            // Otherwise --> FCFS

    private static MultiLevel multilevel;

    protected MultiLevel () {
        sjfScheduler = new SJF();
        roundRobinScheduler = new RoundRobin();
        fcfsScheduler = new FCFS();
        runBackground = false;
    }

    public static MultiLevel getInstance() {
        if(multilevel == null) {
            multilevel = new MultiLevel();
        }
        return multilevel;
    }

    /**
     * TODO : CONSIDER CONVERTING THIS BACK TO BEING BASED ON BURST SIZE
     * This implements Multilevel scheduling with priorities based on memory size
     * in SJF or Round Robin queues
     * @param process
     * @throws IllegalArgumentException
     */
    public synchronized void scheduleProcess(PCB process) throws IllegalArgumentException {
        if(process.getCurrentState() != ProcessState.STATE.READY)
            throw new IllegalArgumentException("Process must be in READY state");

        int estimatedRunTime = process.getEstimatedRunTime();

        System.out.format("--MultiLevel-- process id : %d ", process.getPid());

        if(estimatedRunTime <= SJF_QUANTUM) {
            process.setNextBurst(process.getEstimatedRunTime());
            System.out.format("---->MultiLevel SJF---> est Run time : %d\n", process.getNextBurst());
            sjfScheduler.addToQueue(process);
        } else if(estimatedRunTime <= RR_SCHED_QUANTUM) {
            process.setNextBurst(RR_TIME_QUANTUM);
            System.out.format("---->MultiLevel RR---> est Run time : %d\n", process.getNextBurst());
            roundRobinScheduler.addToQueue(process);
        } else {
            process.setNextBurst(process.getEstimatedRunTime());
            System.out.format("---->MultiLevel FCFS---> est Run time : %d\n", process.getNextBurst());
            fcfsScheduler.addToQueue(process);
        }
    }

    /**
     * Get the next process to execute --> Exhaust each queue in order
     * @return
     */
    public synchronized PCB getNextProcess() {
        if(sjfScheduler.getQueue().size() > 0 && !runBackground ) {
            System.out.println("Retrieving from SJF");
            return sjfScheduler.getNextFromQueue();
        }
        if(roundRobinScheduler.getQueue().size() > 0 && !runBackground ) {
            System.out.println("Retrieving from RoundRobin");
            return roundRobinScheduler.getNextFromQueue();
        }
        if(fcfsScheduler.getQueue().size() > 0) {
            System.out.println("Retrieving from FCFS");
            return fcfsScheduler.getNextFromQueue();
        }
        return null;
    }

    /**
     * @return A count of all the processes currently in the ready queues
     */
    public synchronized int getReadyCount() {
        int readyProcesses = 0;
        readyProcesses += sjfScheduler.getQueue().size();
        readyProcesses += roundRobinScheduler.getQueue().size();
        readyProcesses += fcfsScheduler.getQueue().size();
        return readyProcesses;
    }










}
