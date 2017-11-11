package Sys;

import Sys.Scheduling.MultiLevel;

/**
 * @author Will Russell on 11/8/17
 * @project OS_Simulator
 *
 * Create the dispatcher as a singleton
 *
 */
public class Dispatcher {
    // Should get the next process to execute from the scheduler

    private static Dispatcher dispatcher;
    private static MultiLevel processScheduler = new MultiLevel();


    protected Dispatcher() {}

    public static Dispatcher getInstance() {
        if(dispatcher == null) {
            dispatcher = new Dispatcher();
        }
        return dispatcher;
    }

    public PCB getNextProcessToExecute() {
        return processScheduler.getNextProcess();
    }

    public void addProcessToReadyQueue(PCB process) {
        // Switch the process to the READY state
        process.setCurrentState(ProcessState.STATE.READY);
        processScheduler.scheduleProcess(process);
    }






}
