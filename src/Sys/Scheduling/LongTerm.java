package Sys.Scheduling;

import Sys.Dispatcher;
import Sys.PCB;
import Sys.ProcessState;

import java.util.ArrayList;


/**
 * @project OS_Simulator
 *  There should be only one Long Term Scheduler
 *  --> Therefore a Singleton
 *
 *  This scheduler should determine which processes
 *
 */
public class LongTerm extends Scheduler {

    ArrayList<PCB> queue = new ArrayList<>();

    private static LongTerm longTermScheduler;
    private Dispatcher dispatcher = Dispatcher.getInstance();

    protected LongTerm() { }

    public static LongTerm getInstance() {
        if(longTermScheduler == null) {
            longTermScheduler = new LongTerm();
        }
        return longTermScheduler;
    }

    public void loadAndInitializeNewProcess(PCB process) {

    }

    public void moveNewProcessToAppropriateQueue() {
        PCB process =  getNextFromQueue(queue);
        if (process.hasAllResourcesAvailable()) {
            process.setCurrentState(ProcessState.STATE.READY);
            dispatcher.addProcessToReadyQueue(process);
        } else {
            process.setCurrentState(ProcessState.STATE.WAIT);

        }
    }

    public void addToQueue(PCB process) {
        queue.add(process);
    }

}
