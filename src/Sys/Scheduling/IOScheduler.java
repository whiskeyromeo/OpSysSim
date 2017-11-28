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


    public void addToIOEventToQueue(PCB process) {
        IOEvent event = new IOEvent(process, IOBurst.generateIO());
        ioQueue.add(event);
    }

    public synchronized void reScheduleCompleteProcesses() {
        int timePassed;
        if(ioQueue.size() > 0) {
            IOEvent event = ioQueue.remove(0);
            timePassed = kernel.getSystemClock() - event.originTime;
            if(timePassed < event.numCycles) {
                ioQueue.add(0, event);
            } else {
//                System.out.println("Rescheduling process");
                event.process.setCurrentState(ProcessState.STATE.READY);
                multiLevel.scheduleProcess(event.process);

            }
        }

    }


    public List<PCB> streamIOQueue() {
        List<PCB> processes = ioQueue.stream()
                .map(p -> p.process).collect(Collectors.toList());

        return processes;
    }

    public int getIOQueueSize() {
        return ioQueue.size();
    }


    public List<PCB> getProcessesFromIOQueue() {
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