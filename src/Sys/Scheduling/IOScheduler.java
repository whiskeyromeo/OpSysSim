package Sys.Scheduling;

import Sys.IOBurst;
import Sys.Kernel;
import Sys.PCB;
import Sys.ProcessState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @project OS_Simulator
 */

public class IOScheduler {

    private static IOScheduler ioScheduler;
    private static Kernel kernel = Kernel.getInstance();
    private static MultiLevel multiLevel = MultiLevel.getInstance();

    private static ArrayList<IOEvent> ioQueue = new ArrayList<>();

    protected IOScheduler(){ }

    public static IOScheduler getInstance() {
        if(ioScheduler == null) {
            ioScheduler = new IOScheduler();
        }
        return ioScheduler;
    }


    /**
     * Add a process to the IO queue
     * @param process the process to enqueue
     */
    public synchronized void addToIOEventToQueue(PCB process) {
        IOEvent event = new IOEvent(process, IOBurst.generateIO());
        ioQueue.add(event);
    }

    /**
     * Reschedule a process which has completed its IO service time
     */
    public synchronized void reScheduleCompleteProcesses() {
        int timePassed;
        if(ioQueue.size() > 0) {
            IOEvent event = ioQueue.remove(0);
            timePassed = kernel.getSystemClock() - event.originTime;
            if(timePassed < event.numCycles) {
                ioQueue.add(0, event);
            } else {
                //System.out.println("Rescheduling process");
                event.process.decrementEstimatedRunTime(event.numCycles);
                event.process.setCurrentState(ProcessState.STATE.READY);
                multiLevel.scheduleProcess(event.process);

            }
        }

    }

    /**
     * @param process the process to be removed from the queue
     */
    public synchronized void removeProcessFromIOQueue(PCB process){
        int procIndex = -1;
        for(IOEvent ioEvent: ioQueue) {
            if(ioEvent.process == process) {
                procIndex = ioQueue.indexOf(process);
            }
        }
        if(procIndex >= 0) {
            ioQueue.remove(procIndex);
        }
    }


    public synchronized int getIOQueueSize() {
        return ioQueue.size();
    }


    public synchronized List<PCB> getProcessesFromIOQueue() {
        List<PCB> processes = new ArrayList<>();
        for(IOEvent event : ioQueue) {
            processes.add(event.process);
        }
        return processes;
    }

    public void resetIOQueue() {
        ioQueue.clear();
    }

}

/**
 * A class to keep track of the arrival time of a process to the IO Queue
 */
class IOEvent {

    private static Kernel kernel = Kernel.getInstance();
    public int originTime;
    public int numCycles;
    public PCB process;

    public IOEvent(PCB process, int cycleTime) {
        this.process = process;
        this.numCycles = cycleTime;
        this.originTime = kernel.getSystemClock();
    }
}