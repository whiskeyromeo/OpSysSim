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


    public static final int REGISTER_COUNT = 64;
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
        this.registerSet = Register.instantiateRegisterSet(REGISTER_COUNT);
        this.cpuID = cpuId;
        this.clock = 0;
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
    public void run() {
        // CPU EXECUTES A PROCESS
        while(multiLevel.getReadyCount() != 0) {
            //System.out.format("Ready count is : %d\n", multiLevel.getReadyCount());
            execute();
        }
        System.out.println("-----ALL PROCESSES EXECUTED-------");
    }

    public void execute() {
        String[] currentInstruction;
        int programCounter;
        int currentBurst, nextBurst;
        int ioBurst;
        int pid;
        int calcBurst;

        setActiveProcess();

        pid = this.activeProcess.getPid();
        instructionSet = this.activeProcess.getInstructions();
        programCounter = this.activeProcess.getProgramCounter();


        currentBurst = 0;
        calcBurst = this.activeProcess.getBurstTime();
        nextBurst = this.activeProcess.getNextBurst();

        //************ ERROR CHECKING ******************//


//        System.out.format("Calculating for process %s\n", pid);
        System.out.format("instructions : ");
        for(String command : instructionSet) {
            System.out.format("%s , ", command);
        }
        System.out.format("\n");
//
//        System.out.format("Program Counter is %d\n", programCounter);
//        if(instructionSet.size() < programCounter) {
//            System.out.format("Current instruction is : %s \n", instructionSet.get(programCounter));
//        } else {
//            System.out.println("No more Instructions, only calc left");
//        }
//        System.out.format("Calc burst for %d is %d\n",pid, calcBurst );
////        System.out.format("Current burst for %d is %d\n",pid, currentBurst );
//        System.out.format("Next burst for %d is %d\n", pid, nextBurst);

        // *********** END ERROR CHECKING *****************//

        while(currentBurst <= nextBurst && !signalInterrupt ) {

            if(( calcBurst == 0 && !signalInterrupt) && (programCounter != instructionSet.size())) {

                currentInstruction = instructionSet.get(programCounter).split(" ");

                switch (currentInstruction[0]) {
                    case "CALCULATE":
                        calcBurst = Integer.valueOf(currentInstruction[1]);
                        System.out.format("CALCULATE %s for pid : %d\n", currentInstruction[1], pid);
                        break;
                    case "I/O":
                        calcBurst = IOBurst.generateIO();
                        // TODO : BLOCKED PROCESSES FALL THROUGH --> Need to schedule properly
                        activeProcess.setCurrentState(ProcessState.STATE.BLOCKED);
                        System.out.format("I/O %d for pid : %d\n",calcBurst,pid);
//                        signalInterrupt = true;
                        break;
                    case "YIELD":
                            System.out.format("YIELD for pid : %d\n", pid);
    //                        signalInterrupt = true;
                        break;
                    case "OUT":
                            System.out.format("OUT for pid : %d\n", pid);
//                            activeProcess.printPCBInfo();
                        break;
                    default:
                        System.out.print("BROKE TO SWITCH DEFAULT");
                        return;

                }
                programCounter += 1;
                activeProcess.setProgramCounter(programCounter);
            } else {
                currentBurst++;
                calcBurst--;
//                if(calcBurst == 0) {
//                    System.out.format("\nprocess done cycling --> Cycles remaining : %d\n", nextBurst - currentBurst);
//                }
            }
            advanceClock();

            // Ensure that all Calculations have been done before the process can exit
            if(programCounter >= instructionSet.size() && calcBurst == 0) {
                exitProcess();
                System.out.format("process %d exited\n", pid);
                break;
            }

        } // End while

        // Process has been preempted --> save current state
        if(this.activeProcess.getCurrentState() == ProcessState.STATE.RUN) {
            System.out.format("Preempting process %d\n", pid);
            System.out.format("Current Burst : %d, calcBurst : %d \n", currentBurst, calcBurst);
            preemptCurrentProcess(calcBurst, currentBurst);
        }

        // TODO: CONVERT THIS TO SOMETHING THAT ACTUALLY COMMUNICATES WITH INTERRUPT HANDLER/IO
        if(this.activeProcess.getCurrentState() == ProcessState.STATE.BLOCKED) {
            System.out.format("Preempting BLOCKED process %d\n", pid);
            System.out.format("Current Burst : %d, calcBurst : %d \n", currentBurst, calcBurst);
            preemptCurrentProcess(calcBurst, currentBurst);
        }

        System.out.format("Finished calculating for process %s\n", this.activeProcess.getPid());
        System.out.println("---------------------------------");


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
