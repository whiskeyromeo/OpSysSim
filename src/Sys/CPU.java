package Sys;

import Sys.Memory.MemoryManager;
import Sys.Memory.Register;
import Sys.Scheduling.MultiLevel;

import java.util.ArrayList;

/**
 * @author Capitan on 11/7/17
 * @project OS_Simulator
 *
 *  Each CPU should be implemented with a thread
 *
 */
public class CPU implements Runnable {


    public static final int REGISTER_COUNT = 32;
    public static final int CPU_COUNT = 4;
    public static final int CACHE_SIZE = 120;


    private Register[] registerSet;     // The amount of memory available to a CPU
    private int cpuID;      // Figure we need an id to keep track of each CPU in case of multiple cores
    private int clock;
    private PCB activeProcess; //The currently active process in the CPU
    private ArrayList<String> instructionSet; //The instruction set of the currently active process
    private boolean signalInterrupt = false; //Temp variable to use in the switch statement
    private Dispatcher dispatcher = Dispatcher.getInstance();
    private MultiLevel multiLevel = MultiLevel.getInstance();
    private MemoryManager memoryManager = MemoryManager.getInstance();



    public CPU(int cpuId) {
        this.registerSet = new Register[REGISTER_COUNT];
        this.cpuID = cpuId;
        this.clock = 0;
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
    public void run() {
        // CPU EXECUTES A PROCESS
        while(multiLevel.getReadyCount() != 0) {
            //TODO: Implement a check to see whether any processes
            System.out.format("Ready count is : %d\n", multiLevel.getReadyCount());
            execute();
        }

    }

    public void execute() {
        String[] currentInstruction;
        int programCounter;
        int currentBurst;
        int ioBurst;

        setActiveProcess();

        instructionSet = this.activeProcess.getInstructions();
        programCounter = this.activeProcess.getProgramCounter();

        while( programCounter < instructionSet.size() ) {

            currentBurst = this.activeProcess.getBurstTime();

            if(currentBurst == 0 && !signalInterrupt) {

                currentInstruction = instructionSet.get(programCounter).split(" ");

                switch (currentInstruction[0]) {
                    case "CALCULATE":
                        activeProcess.setBurstTime(Integer.valueOf(currentInstruction[1]));
                        System.out.format("CALCULATE %s for pid : %d\n", currentInstruction[1], activeProcess.getPid());
                        break;
                    case "I/O":
                        ioBurst = IOBurst.generateIO();
                        activeProcess.setBurstTime(ioBurst);
                        activeProcess.setCurrentState(ProcessState.STATE.BLOCKED);
                        System.out.format("I/O %d for pid : %d\n",ioBurst, activeProcess.getPid());
                        signalInterrupt = true;
                        break;
                    case "YIELD":
                        System.out.format("YIELD for pid : %d", activeProcess.getPid());
//                        signalInterrupt = true;
                        break;
                    case "OUT":
                        activeProcess.printPCBInfo();
                        break;
                    default:
                        return;

                }
                activeProcess.setProgramCounter(programCounter++);
            } else {
//                System.out.format("process cycling --> Cycles remaining : %d\n", currentBurst);
                this.activeProcess.decrementBurstTime();
                // HACK
                if(this.activeProcess.getBurstTime() == 0) {
                    if(this.activeProcess.getCurrentState() == ProcessState.STATE.BLOCKED) {
                        System.out.format("Process %d is now unblocked!\n", this.activeProcess.getPid());
                        this.activeProcess.setCurrentState(ProcessState.STATE.RUN);
                        signalInterrupt = false;
                    }
                    activeProcess.setProgramCounter(programCounter++);
                }
            }
            advanceClock();
        }
        System.out.println("Out of execute");

        this.activeProcess.exit();

    }

    public void exitProcess() {
        memoryManager.deallocateMemory(this.activeProcess.getMemRequired());
        this.activeProcess.exit();


    }

    public void writeLog() {
        
    }




}
