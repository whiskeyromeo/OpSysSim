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
    private static MultiLevel processScheduler = MultiLevel.getInstance();


    protected Dispatcher() {}

    public static Dispatcher getInstance() {
        if(dispatcher == null) {
            dispatcher = new Dispatcher();
        }
        return dispatcher;
    }

    /**
     * Process should retrieve the next process to execute from
     * the short term scheduler and set it to RUN on the CPU
     * @return
     */
    public synchronized PCB getNextProcessToExecute() {
        PCB process;
        process = processScheduler.getNextProcess();
//        System.out.format("---Dispatcher --> estimated Run time is : %d\n",process.getEstimatedRunTime());
        if(process != null) {
            process.setCurrentState(ProcessState.STATE.RUN);
        }
        return process;
    }


    public void removeCurrentProcess(PCB process) {
        if(process.getCurrentState() == ProcessState.STATE.READY)
            processScheduler.scheduleProcess(process);
        else
            System.err.println("-------ERR --> PROCESS NOT IN READY STATE------");
    }

    public void endProcess(PCB process) {

    }





}
