package Sys.Scheduling;

import Sys.Kernel;
import Sys.PCB;
import Sys.ProcessState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

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

    public static int clockTime;

    // Set the time quantum to determine which scheduler should be called upon
    public final int SJF_QUANTUM = 30;      // If a process has a burst time of < 10 --> sjf
    public final int RR_SCHED_QUANTUM = 250;       // If a process has a burst time of < 40 --> rrobin
    public final int RR_TIME_QUANTUM = 30;
                                            // Otherwise --> FCFS

    public static int sjfScheduled = 0;
    public static int fcfsScheduled = 0;
    public static int rrScheduled = 0;

    public static int sjfWaitTime = 0;
    public static int rrWaitTime = 0;
    public static int fcfsWaitTime = 0;


    private static MultiLevel multilevel;

    protected MultiLevel () {
        sjfScheduler = new SJF();
        roundRobinScheduler = new RoundRobin();
        fcfsScheduler = new FCFS();
    }

    public static MultiLevel getInstance() {
        if(multilevel == null) {
            multilevel = new MultiLevel();
        }
        return multilevel;
    }

    /**
     *
     * This implements Multilevel scheduling with priorities based on estimated run time
     * in SJF or Round Robin queues
     * @param process
     * @throws IllegalArgumentException
     */
    public synchronized void scheduleProcess(PCB process) throws IllegalArgumentException {
        if(process.getCurrentState() != ProcessState.STATE.READY)
            throw new IllegalArgumentException("Process must be in READY state");

        int estimatedRunTime = process.getEstimatedRunTime();
        // System.out.println("Estimated run time for proc : " + process.getPid() + " is " + estimatedRunTime);
        process.setArrivalTime(Kernel.getStaticSystemClock());

        if(estimatedRunTime <= SJF_QUANTUM) {
            process.setNextBurst(process.getEstimatedRunTime());
            sjfScheduler.addToQueue(process);
        } else if(estimatedRunTime <= RR_SCHED_QUANTUM) {
            process.setNextBurst(RR_TIME_QUANTUM);
            roundRobinScheduler.addToQueue(process);
        } else {
            process.setNextBurst(process.getEstimatedRunTime());
            fcfsScheduler.addToQueue(process);
        }
    }

    /**
     * Get the next process to execute --> Exhaust each queue in order
     * @return
     */
    public synchronized PCB getNextProcess() {
        PCB process;
        if(sjfScheduler.getQueue().size() > 0 && !runBackground ) {
            sjfScheduled++;
//            System.out.println("Retrieving from SJF");
            process = sjfScheduler.getNextFromQueue();
            sjfWaitTime = updateWaitTime(sjfWaitTime, sjfScheduled, process);

            return process;
        }
        if(roundRobinScheduler.getQueue().size() > 0 && !runBackground ) {
            rrScheduled++;

            process = roundRobinScheduler.getNextFromQueue();
            rrWaitTime = updateWaitTime(rrWaitTime, rrScheduled, process);
            return process;
        }
        if(fcfsScheduler.getQueue().size() > 0) {
            fcfsScheduled++;
//            System.out.println("Retrieving from FCFS");
            process =  fcfsScheduler.getNextFromQueue();
            fcfsScheduled = updateWaitTime(fcfsWaitTime, fcfsScheduled, process);
            return process;
        }
//        System.out.println("-----MultiLevel --> no more processes to be had");
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


    public Stream<PCB> getAllInReadyStream() {

        ArrayList<PCB> sjfQueue = sjfScheduler.getQueue();
        ArrayList<PCB> rrQueue = roundRobinScheduler.getQueue();
        ArrayList<PCB> fcfsQueue = fcfsScheduler.getQueue();

        return Stream.of(sjfQueue, rrQueue, fcfsQueue)
                .flatMap(Collection::stream);

    }

    public synchronized ArrayList<PCB> getReadyQueue() {
        ArrayList<PCB> newQueue = new ArrayList<>();
        ArrayList<PCB> sjfQueue = sjfScheduler.getQueue();
        ArrayList<PCB> rrQueue = roundRobinScheduler.getQueue();
        ArrayList<PCB> fcfsQueue = fcfsScheduler.getQueue();
        newQueue.addAll(sjfQueue);
        newQueue.addAll(rrQueue);
        newQueue.addAll(fcfsQueue);
        return newQueue;
    }

    public void resetReadyQueues() {
        sjfScheduler.resetQueue();
        fcfsScheduler.resetQueue();
        roundRobinScheduler.resetQueue();
        sjfScheduled = 0;
        fcfsScheduled = 0;
        rrScheduled = 0;
        sjfWaitTime = 0;
        rrWaitTime = 0;
        fcfsWaitTime = 0;
    }



    public List<PCB> getReadyQueues() {
        List<PCB> readyList = new ArrayList<>();
        readyList.addAll(sjfScheduler.getQueue());
        readyList.addAll(fcfsScheduler.getQueue());
        readyList.addAll(roundRobinScheduler.getQueue());
        return readyList;
    }

    public int updateWaitTime(int currentWait, int n, PCB process) {
        int procWait = Kernel.getStaticSystemClock() - process.getArrivalTime();
        int newWait = (((n-1)*currentWait)+procWait)/n;
        return newWait;
    }





}
