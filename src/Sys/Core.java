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
    private int programCounter;
    private int numInstructions;
    private int currentBurst;
    private int calcBurst;
    private int burstRemaining = 0;
    private int nextBurst;
    private boolean timeout = false;
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

    /**
     * Acts as the Core cache, setting the attributes of the process locally
     */
    public void setActiveProcess() {

        if(this.activeProcess == null) {
            this.activeProcess = dispatcher.getNextProcessToExecute();

            if(this.activeProcess != null) {
                RunningQueue.addToList(this.activeProcess);
            }
        }

    }

    /**
     * Puts the core into a running state
     */
    public void run() {
        if(GUI.isActive) {
            runForGUI();
        } else {
            runForSim();
        }

    }

    /**
     * Runs the core in a JavaFx safe manner
     */
    public void runForGUI() {
        // Initialize to sanitize for multiple threads
        if (!isStarted) {
            isStarted = true;
        }
        InterruptHandler.checkForDeviceInterrupt(this.coreId);

        if(!InterruptHandler.deviceInterruptSignalled || InterruptHandler.serviceID != this.coreId) {
            //System.out.println("Core " + this.coreId + " is running");
            execute();

            if (this.activeProcess != null && this.activeProcess.getCurrentState() != ProcessState.STATE.RUN) {
                RunningQueue.removeFromList(this.activeProcess);
                this.activeProcess = null;
                updateTableVals();
            } else if (this.activeProcess != null) {
                //System.out.println("run queue size : " + RunningQueue.getSize());
            }
        } else {
            //System.out.println("Core " + this.coreId + " is servicing the interrupt");
            InterruptHandler.serviceDeviceInterrupt();
        }

    }

    /**
     * Runs the core in a manner suited for multithreading
     */
    public void runForSim() {
        while(!InterruptHandler.interruptSignalled) {
            runForGUI();
        }
    }

    /**
     * Main code --> determines the execution of a process
     * in the core.
     */
    public void execute() {
        currentBurst = 0;
        String[] command;

        setActiveProcess();

        //Make sure a process was set before proceeding
        if(this.activeProcess == null) {
            return;
        }

        // Check if there are any calculations remaining to be done
        // in this cycle
        if(calcBurst == 0) {
            calcBurst = this.activeProcess.getBurstTime();
        }


        nextBurst = this.activeProcess.getNextBurst();
        programCounter = this.activeProcess.getProgramCounter();
        numInstructions = this.activeProcess.getInstructions().size();

        // if the process is in the critical section(I/O), it should continue until the
        // section is done if an interrupt is signalled
        // Otherwise continue until some interrupt pulls the program out of
        // the processor
        if ( programCounter < numInstructions && !signalInterrupt ) {
            //System.out.println("Starting process" + this.activeProcess.getPid() + ", nextburst : " + nextBurst +
             //       ",instruction : " + this.activeProcess.getInstructions().get(programCounter));

            // Get a new instruction from the set
            if(calcBurst == 0 && !timeout) {
                command = this.activeProcess.getInstructions().get(programCounter).split(" ");

                switch (command[0]) {
                    case "CALCULATE":
                        executeCalculate(command[1]);
                        break;
                    case "I/O":
                        executeIO();
                        return;
                    case "OUT":
                        executeOut();
                        break;
                    case "YIELD":
                        executeYield();
                        return;
                    default:
                        System.out.print("BROKE TO SWITCH DEFAULT : command : " + command[0] );
                        return;
                }

                programCounter += 1;
                this.activeProcess.setProgramCounter(programCounter);

            } else if(calcBurst == 0 && timeout) {
                //Preempt the process --> out of cycle time
                timeoutProcess();
                return;
            } else if(calcBurst > 0 && nextBurst >= calcBurst) {
                // process should be preempted(Round robin) --> set the burst time accordingly
                // System.out.println("proc : " + this.activeProcess.getPid() + ", calcburst : " + calcBurst);
                calcBurst--;
                this.activeProcess.setBurstTime(calcBurst);
            }
            currentBurst++;

        } else {
            // The process has completed all calculations and can exit
            System.out.println("process " + this.activeProcess.getPid() + " has exited");
            ioScheduler.removeProcessFromIOQueue(this.activeProcess);
            memoryManager.deallocateMemory(this.activeProcess.getMemRequired());
            this.activeProcess.exit();
            updateGui(this.activeProcess.getPCBLine());
        }
    }

    /**
     * update the table in the GUI
     */
    public synchronized void updateTableVals() {
        try {
            GUI.updateTableValues();
        } catch(Throwable e) {
          //System.out.println("...expected table val err");
        }
    }

    public synchronized void updateGui(String string) {
        try {
            GUI.addLine(string);
        } catch(Throwable e) {
            //System.out.println("...expected table val err");
        }

    }

    public synchronized void delayForUpdate(int millis) {
        try{
            Thread.currentThread().sleep(millis);
        }catch(Throwable e) {
            e.printStackTrace();
        }
    }

    public synchronized void executeYield() {
        //System.out.print("yield : ");
        try {
            semaphore.acquire();
            PCB previousProcess = this.activeProcess;
            this.activeProcess.setBurstTime(calcBurst);
            this.activeProcess.setProgramCounter(programCounter+1);
            // System.out.println("Decrementing " + this.activeProcess.getPid() + " burst by " + currentBurst);
            this.activeProcess.decrementEstimatedRunTime(currentBurst);
            this.activeProcess.incrementCriticalTime(1);
            if((memoryManager.getCurrentMemory() > (this.activeProcess.getMemAllocated()/2))
                    && this.activeProcess.getChildren().size() < 3 && this.activeProcess.getPid() < 10) {
                messageQueue.add(String.format("Process %d had a child!", this.activeProcess.getPid()));
                this.activeProcess = this.activeProcess._fork();
            }

            if(previousProcess == this.activeProcess){
                this.activeProcess.setCurrentState(ProcessState.STATE.READY);
                multiLevel.scheduleProcess(this.activeProcess);
            }
            semaphore.release();
        } catch(Throwable e) {
            e.printStackTrace();
        }

        //System.out.println("Preempting process as part of yield");
    }

    public void executeIO() {
        //System.out.print("i/o : ");
        this.activeProcess.incrementIoRequests();
        //CPU.messageQueue.add(String.format("Process %d entered I/O", this.activeProcess.getPid()));
        programCounter += 1;
        this.activeProcess.setProgramCounter(programCounter);
        this.activeProcess.setCurrentState(ProcessState.STATE.BLOCKED);
        ioScheduler.addToIOEventToQueue(this.activeProcess);
    }

    public void executeCalculate(String cycles) {
        int resetCalc;
        calcBurst = Integer.parseInt(cycles);
        if(calcBurst > nextBurst) {
            resetCalc = calcBurst - nextBurst;
            String reset = "CALCULATE " + resetCalc;
            this.activeProcess.getInstructions().set(programCounter, reset);
            programCounter = programCounter - 1;
            calcBurst = nextBurst;
            timeout = true;
        }
        this.activeProcess.setBurstTime(calcBurst);
    }

    public void executeOut() {
        String output = this.activeProcess.getPCBOutput();
        if(!CPU.messageQueue.isEmpty()) {
            String message = CPU.messageQueue.poll();
            //System.out.println("Process " + this.activeProcess.getPid() + " received message : " + message);
            output += "\n\t Received message : " + message;
        }
        //System.out.println(output);
        // SEND INFORMATION TO THE GUI
        updateGui(output);
    }

    public void timeoutProcess() {
        //Preempt the process --> out of cycle time
        this.activeProcess.setBurstTime(burstRemaining);
        this.activeProcess.decrementEstimatedRunTime(nextBurst);
        this.activeProcess.setCurrentState(ProcessState.STATE.READY);
        multiLevel.scheduleProcess(this.activeProcess);
        timeout = false;
    }



    public void setActiveProcess(PCB process) {
        this.activeProcess = process;
    }
    public PCB getActiveProcess() { return this.activeProcess; }
    public void advanceClock() {
        this.clock++;
    }


}
