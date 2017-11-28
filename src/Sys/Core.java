package Sys;

import Sys.Memory.MemoryManager;
import Sys.Scheduling.IOScheduler;
import Sys.Scheduling.MultiLevel;
import User_space.GUI;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * @project OS_Simulator
 */
public class Core implements Runnable{

    private int coreId;
    private int clock;
    private PCB activeProcess;
    private boolean isStarted;
    private boolean signalInterrupt = false; //Temp variable to use in the switch statement

    private Dispatcher dispatcher = Dispatcher.getInstance();
    private MultiLevel multiLevel = MultiLevel.getInstance();
    private IOScheduler ioScheduler = IOScheduler.getInstance();
    private MemoryManager memoryManager = MemoryManager.getInstance();

    private Semaphore semaphore;
    private BlockingQueue<String> messageQueue;

    public Core(Semaphore parentSemaphore, BlockingQueue<String> parentQueue, int coreId) {
        this.coreId = coreId;
        this.clock = 0;
        this.semaphore = parentSemaphore;
        this.messageQueue = parentQueue;
        this.isStarted = false;
    }

    public void setActiveProcess() {
        this.activeProcess = dispatcher.getNextProcessToExecute();
    }


    public void run() {
        if(!isStarted) {
            isStarted = true;
        }

        execute();
        RunningQueue.removeFromList(this.activeProcess);
        this.activeProcess = null;


    }

    public void execute() {
        int burstRemaining, ioBurst, calcBurst, nextBurst, programCounter, numInstructions;
        int currentBurst = 0;
        String[] command;
        boolean timeout = false;
        boolean isCritical = false;

        setActiveProcess();

        if(this.activeProcess == null) {
            return;
        }

        RunningQueue.addToList(this.activeProcess);
        //System.out.println("--------------" + RunningQueue.runningList.size());

        updateTableVals();

        try{
            Thread.currentThread().sleep(200);
        }catch(Throwable e) {
            e.printStackTrace();
        }


        burstRemaining = 0;
        calcBurst = this.activeProcess.getBurstTime();
        nextBurst = this.activeProcess.getNextBurst();
        programCounter = this.activeProcess.getProgramCounter();
        numInstructions = this.activeProcess.getInstructions().size();

        // if the process is in the critical section(I/O), it should continue until the
        // section is done if an interrupt is signalled
        // Otherwise continue until some interrupt pulls the program out of
        // the processor
        while ( programCounter < numInstructions && !signalInterrupt ) {

            if(calcBurst == 0 && !timeout) {
                command = this.activeProcess.getInstructions().get(programCounter).split(" ");

                switch (command[0]) {
                    case "CALCULATE":
                        System.out.print("calculate " + command[1] + " : ");
                        calcBurst = Integer.parseInt(command[1]);
                        this.activeProcess.setBurstTime(calcBurst);
                        break;
                    case "I/O":
                        System.out.print("i/o : ");
                        this.activeProcess.incrementIoRequests();
                        CPU.messageQueue.add(String.format("Process %d entered I/O", this.activeProcess.getPid()));
                        programCounter += 1;
                        this.activeProcess.setProgramCounter(programCounter);
                        this.activeProcess.setCurrentState(ProcessState.STATE.BLOCKED);
                        ioScheduler.addToIOEventToQueue(this.activeProcess);
                        return;
                    case "OUT":
                        String output = this.activeProcess.getPCBOutput();
                        if(!CPU.messageQueue.isEmpty()) {
                            String message = CPU.messageQueue.poll();
                            System.out.println("Process " + this.activeProcess.getPid() + " received message : " + message);
                            output += "\n\t Received message : " + message;
                        }
                        //System.out.println(output);
                        // SEND INFORMATION TO THE GUI
                        try {
                            GUI.addLine(output);
                        } catch(Throwable e) {
                            System.out.println("...got expected error");
                        }

                        System.out.print("out : ");
                        break;
                    case "YIELD":
                        System.out.print("yield : ");
                        this.activeProcess.setBurstTime(calcBurst);
                        this.activeProcess.setProgramCounter(programCounter+1);
                        this.activeProcess.decrementEstimatedRunTime(currentBurst);
                        this.activeProcess.setCurrentState(ProcessState.STATE.READY);
                        multiLevel.scheduleProcess(this.activeProcess);
                        System.out.println("Preempting process as part of yield");
                        return;
                    default:
                        System.out.print("BROKE TO SWITCH DEFAULT : command : " + command[0] );
                        return;
                }

                programCounter += 1;
                this.activeProcess.setProgramCounter(programCounter);

            } else if(calcBurst == 0 && timeout) {
                //Preempt the process --> out of cycle time
                this.activeProcess.setBurstTime(burstRemaining);
                this.activeProcess.decrementEstimatedRunTime(nextBurst);
                this.activeProcess.setCurrentState(ProcessState.STATE.READY);
                multiLevel.scheduleProcess(this.activeProcess);
//                System.out.println("Preempting process exceeding time quantum");
                return;
            } else if(calcBurst > 0 && nextBurst >= calcBurst) {
                calcBurst--;
                this.activeProcess.setBurstTime(calcBurst);
            } else if(nextBurst < calcBurst) {
                // set remaining burst time for later
                burstRemaining = calcBurst - nextBurst;
                timeout = true;
                // Set the remaining calculation time to the time of the burst allocated
                calcBurst = nextBurst;
            }

            currentBurst++;
            CPU.advanceTotalCycles();

        } //else {
            System.out.println("process " + this.activeProcess.getPid() + " has exited");
            memoryManager.deallocateMemory(this.activeProcess.getMemRequired());
            this.activeProcess.exit();
            updateGui(this.activeProcess.getPCBLine());
        //}
    }

    public void updateTableVals() {
        try {
            GUI.updateTableValues();
        } catch(Throwable e) {
          //System.out.println("...expected table val err");
        }
    }

    public void updateGui(String string) {
        try {
            GUI.addLine(string);
        } catch(Throwable e) {
            //System.out.println("...expected table val err");
        }

    }

    public void setActiveProcess(PCB process) {
        this.activeProcess = process;
    }
    public PCB getActiveProcess() { return this.activeProcess; }
    public void advanceClock() {
        this.clock++;
    }


}
