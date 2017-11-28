package Sys;

import Sys.Memory.MemoryManager;
import Sys.Memory.Register;
import Sys.Scheduling.MultiLevel;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

/**
 * @project OS_Simulator
 */
public class Core_v2 implements Runnable {

    public static final int REGISTER_COUNT = 64;

    Kernel kernel = Kernel.getInstance();

    private Register[] registerSet;     // The amount of memory available to a CPU
    private int coreId;      // Figure we need an id to keep track of each CPU in case of multiple cores
    private int clock;
    private PCB activeProcess; //The currently active process in the CPU
    private ArrayList<String> instructionSet; //The instruction set of the currently active process
    private boolean signalInterrupt = false; //Temp variable to use in the switch statement
    private Dispatcher dispatcher = Dispatcher.getInstance();
    private MultiLevel multiLevel = MultiLevel.getInstance();
    private MemoryManager memoryManager = MemoryManager.getInstance();

    private Semaphore semaphore;
    private BlockingQueue<String> messageQueue;
    private CopyOnWriteArrayList<PCB> parentRunningQueue;

    public Core_v2(Semaphore parentSemaphore, BlockingQueue<String> parentQueue, int coreId, CopyOnWriteArrayList<PCB> runningQueue) {
        this.registerSet = Register.instantiateRegisterSet(REGISTER_COUNT);
        this.coreId = coreId;
        this.clock = 0;
        this.semaphore = parentSemaphore;
        this.messageQueue = parentQueue;
        this.parentRunningQueue = runningQueue;
    }


    public void addInstructionsToRegisters() {
        ArrayList<String> instructions = this.activeProcess.getInstructions();
        for(int i = 0; i < REGISTER_COUNT; i++) {
            this.registerSet[i].setOccupied(true);
            this.registerSet[i].setData(instructions.get(i));
        }

    }

    public void setActiveProcess() {
        this.activeProcess = dispatcher.getNextProcessToExecute();
        if(this.activeProcess != null)
            this.parentRunningQueue.add(this.activeProcess);

    }


    public void advanceClock() {
        // TODO : I FEEL AS THOUGH THESE SHOULD BE INCREMENTED SEPARATELY
        kernel.advanceClock();
        this.clock++;
    }

    /**
     * To work with the Runnable interface
     * TODO: IMPLEMENT PREEMPTION FOR ROUND ROBIN SCHEDULER
     * Need to increment clock on CPU while decrementing remaining burst time
     *
     */

    public void run(){

        while(multiLevel.getReadyCount() != 0) {
            try {
                Thread.sleep(3000);
            } catch(Exception e) {
                System.out.println("failed to sleep");
            }
            execute();
        }
        if(multiLevel.getReadyCount() == 0) {
            Thread.currentThread().yield();
            System.out.println("Current thread for core " + coreId + " waiting on new input...");
        }
        //System.out.println("-----ALL PROCESSES EXECUTED-------  ---> core " + coreId);
    }

    public void execute() {
        String[] currentInstruction;
        int programCounter;
        int currentBurst, nextBurst;
        int lastIoBurst = 0;
        int pid;
        int calcBurst;

        setActiveProcess();
        if(this.activeProcess == null) {
            return;
        }

        currentBurst = 0;
        pid = this.activeProcess.getPid();
        instructionSet = this.activeProcess.getInstructions();
        programCounter = this.activeProcess.getProgramCounter();
        calcBurst = this.activeProcess.getBurstTime();
        nextBurst = this.activeProcess.getNextBurst();

        while(currentBurst < nextBurst && !signalInterrupt ) {

            if(( calcBurst == 0 && !signalInterrupt) && (programCounter < instructionSet.size())) {

                currentInstruction = instructionSet.get(programCounter).split(" ");
                switch (currentInstruction[0]) {

                    case "CALCULATE":
                        calcBurst = Integer.valueOf(currentInstruction[1]);
                        break;
                    case "I/O":
                        try {
                            semaphore.acquire();
                            // Process acquired lock
                            calcBurst = IOBurst.generateIO();
                            if (calcBurst > nextBurst) {
                                nextBurst = calcBurst + currentBurst;
                            }
                            activeProcess.setCurrentState(ProcessState.STATE.BLOCKED);
                            lastIoBurst = calcBurst;
                        } catch (Throwable e) {
                            // Process failed to Enter critical section --> sent back to ready queue
                            preemptCurrentProcess(calcBurst, currentBurst);
                            e.printStackTrace();
                            return;
                        }
                        break;
                    case "YIELD":
                        //System.out.format("YIELD for pid : %d ---> core %d\n", pid, coreId);

                        getMessageFromMessageQueue(pid);

                        programCounter+=1;
                        this.activeProcess.setProgramCounter(programCounter);

                        preemptCurrentProcess(calcBurst, currentBurst);
                        return;
//                        break;
                    case "OUT":
                        //System.out.format("OUT for pid : %d  ---> core %d\n", pid, coreId);
                        //activeProcess.printPCBInfo();
                        break;
                    default:
                        System.out.print("BROKE TO SWITCH DEFAULT");
                        return;

                }
                programCounter += 1;
                activeProcess.setProgramCounter(programCounter);
            }
            if (calcBurst > 0) {
                currentBurst++;
                calcBurst--;
                this.activeProcess.setBurstTime(calcBurst);
                if(calcBurst == 0 && this.activeProcess.getCurrentState() == ProcessState.STATE.BLOCKED) {
                    //System.out.format("process %d released lock after %d cycles--> leaving block state on core %d\n", pid,lastIoBurst ,coreId);
                    semaphore.release();
                    this.activeProcess.incrementCriticalTime(lastIoBurst);
                    // RETURN PROCESS TO THE READY STATE
                    preemptCurrentProcess(calcBurst, currentBurst);
                    lastIoBurst = 0;
                }
            }
            advanceClock();
            if(programCounter >= instructionSet.size() && calcBurst == 0) {
                exitProcess();
                System.out.format("process %d exited  ---> core %d\n", pid, coreId);
                nextBurst = 0;
                currentBurst = 0;
            }

        } // End while

        // Process has been preempted --> save current state
        if(this.activeProcess != null && this.activeProcess.getCurrentState() == ProcessState.STATE.RUN) {
            preemptCurrentProcess(this.activeProcess.getBurstTime(), currentBurst);
        }
//        this.parentRunningQueue.remove(this.activeProcess);
//        this.activeProcess = null;
    } // end execute




    public void getMessageFromMessageQueue(int pid) {
        String msg = messageQueue.poll();
        if(msg != null) {
            //System.out.format("----!!!---%d received a message! : %s----!!!---\n", pid, msg);
        } else {
            //System.out.format("---No messages for now, adding message ------\n");
            //messageQueue.add("NEW MESSAGE FOR YOU!! MAYBE SOMETHING TO FORK AROUND WITH");
        }

    }


    public void preemptCurrentProcess(int calcBurst, int currentBurst) {
        System.out.println("preempting process " + this.activeProcess.getPid() + " on core " + coreId );
        this.activeProcess.setBurstTime(calcBurst);
        //System.out.format("decrementing estimated time by : %s\n", currentBurst);
        this.activeProcess.decrementEstimatedRunTime(currentBurst);
        this.activeProcess.setCurrentState(ProcessState.STATE.READY);
        multiLevel.scheduleProcess(this.activeProcess);
    }

    public void exitProcess() {
        memoryManager.deallocateMemory(this.activeProcess.getMemRequired());
//        this.activeProcess.exit();
        this.activeProcess = null;

    }




}
