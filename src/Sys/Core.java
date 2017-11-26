package Sys;

import Sys.Memory.MemoryManager;
import Sys.Memory.Register;
import Sys.Scheduling.MultiLevel;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * @project OS_Simulator
 */
public class Core implements Runnable {

    public static final int REGISTER_COUNT = 64;


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

    public Core(Semaphore parentSemaphore, BlockingQueue<String> parentQueue, int coreId) {
        this.registerSet = Register.instantiateRegisterSet(REGISTER_COUNT);
        this.coreId = coreId;
        this.clock = 0;
        this.semaphore = parentSemaphore;
        this.messageQueue = parentQueue;
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
    }


    public void advanceClock() {
        // TODO : I FEEL AS THOUGH THESE SHOULD BE INCREMENTED SEPARATELY
        Kernel.advanceClock();
        this.clock++;
    }

    /**
     * To work with the Runnable interface
     * TODO: IMPLEMENT PREEMPTION FOR ROUND ROBIN SCHEDULER
     * Need to increment clock on CPU while decrementing remaining burst time
     *
     */
    int count = 0;
    public void run(){


        while(multiLevel.getReadyCount() != 0) {
            //System.out.format("Ready count is : %d\n", multiLevel.getReadyCount());
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

        //************ ERROR CHECKING ******************//

        System.out.format("-- Process %d now being calculated by core %d, program counter = %d ----\n", pid, coreId, programCounter);
        System.out.format("Process %d instructions : ", pid);
        for(String command : instructionSet) {
            System.out.format("%s , ", command);
        }
        System.out.format(":: set size = %d\n", instructionSet.size());

//        System.out.format("Program Counter is %d\n", programCounter);
//        if(instructionSet.size() < programCounter) {
//            System.out.format("Current instruction is : %s \n", instructionSet.get(programCounter));
//        } else {
//            System.out.println("No more Instructions, only calc left");
//        }
//        System.out.format("Calc burst for %d is %d\n",pid, calcBurst );
//        System.out.format("Current burst for %d is %d\n",pid, currentBurst );
//        System.out.format("Next burst for %d is %d\n", pid, nextBurst);
//        System.out.format("-WHILE--process %d :: calc burst = %d, nextBurst = %d, currentBurst = %d \n", pid, calcBurst, nextBurst, currentBurst);

        // *********** END ERROR CHECKING *****************//


        int count = 0;
        while(currentBurst < nextBurst && !signalInterrupt ) {

//            if(count > 150) {
//                System.out.println("looping in while");
//            }
//            count++;

            if(( calcBurst == 0 && !signalInterrupt) && (programCounter < instructionSet.size())) {

                currentInstruction = instructionSet.get(programCounter).split(" ");
                switch (currentInstruction[0]) {

                    case "CALCULATE":
                        calcBurst = Integer.valueOf(currentInstruction[1]);
                        System.out.format("CALCULATE %d for pid : %d ---> core %d\n", calcBurst, pid, coreId);
                        break;
                    case "I/O":
                        try {
                            semaphore.acquire();
                            System.out.format("--- process %d acquired lock --> entering block state on core %d\n", pid, coreId);
                            calcBurst = IOBurst.generateIO();
                            if (calcBurst > nextBurst) {
                                nextBurst = calcBurst + currentBurst;
                            }
                            activeProcess.setCurrentState(ProcessState.STATE.BLOCKED);
                            lastIoBurst = calcBurst;
                        } catch (Throwable e) {
                            System.out.format("----%d attempted to enter critical -- failed -- core %d \n", pid, coreId);
                            preemptCurrentProcess(calcBurst, currentBurst);
                            e.printStackTrace();
                            return;
                        }

                        System.out.format("I/O %d for pid : %d ---> core %d\n", calcBurst, pid, coreId);
                        break;
                    case "YIELD":
                        System.out.format("YIELD for pid : %d ---> core %d\n", pid, coreId);

                        getMessageFromMessageQueue(pid);

                        programCounter+=1;
                        this.activeProcess.setProgramCounter(programCounter);
                        preemptCurrentProcess(calcBurst, currentBurst);
                        return;
//                        break;
                    case "OUT":
                        System.out.format("OUT for pid : %d  ---> core %d\n", pid, coreId);
                        activeProcess.printPCBInfo();
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
                    System.out.format("process %d released lock after %d cycles--> leaving block state on core %d\n", pid,lastIoBurst ,coreId);
                    semaphore.release();
                    this.activeProcess.setCurrentState(ProcessState.STATE.RUN);
                    this.activeProcess.incrementCriticalTime(lastIoBurst);
                    lastIoBurst = 0;
                }
            }
            advanceClock();
            if(programCounter >= instructionSet.size() && calcBurst == 0) {
                exitProcess();
                System.out.format("process %d exited  ---> core %d\n", pid, coreId);
                break;
            }

        } // End while

        // Process has been preempted --> save current state
        if(this.activeProcess.getCurrentState() == ProcessState.STATE.RUN) {
            System.out.format("Preempting process %d  ---> core %d\n", pid, coreId);
            System.out.format("Current Burst : %d, calcBurst : %d , nextBurst : %d ---> core %d\n", currentBurst, this.activeProcess.getBurstTime(),nextBurst, coreId);
            preemptCurrentProcess(this.activeProcess.getBurstTime(), currentBurst);
        }

        System.out.format("Finished calculating for process %s  ---> core %d\n", this.activeProcess.getPid(), coreId);
        System.out.println("---------------------------------");

    }


    public void getMessageFromMessageQueue(int pid) {
        String msg = messageQueue.poll();
        if(msg != null) {
            System.out.format("----!!!---%d received a message! : %s----!!!---\n", pid, msg);
        } else {
            System.out.format("---No messages for now, adding message ------\n");
            messageQueue.add("NEW MESSAGE FOR YOU!! MAYBE SOMETHING TO FORK AROUND WITH");
        }

    }



    public void preemptCurrentProcess(int calcBurst, int currentBurst) {
        this.activeProcess.setBurstTime(calcBurst);
        System.out.format("decrementing estimated time by : %s\n", currentBurst);
        this.activeProcess.decrementEstimatedRunTime(currentBurst);
        this.activeProcess.setCurrentState(ProcessState.STATE.READY);
        multiLevel.scheduleProcess(this.activeProcess);
    }

    public void exitProcess() {
        memoryManager.deallocateMemory(this.activeProcess.getMemRequired());
        this.activeProcess.exit();

    }




}
