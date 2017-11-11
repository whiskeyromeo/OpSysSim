package Sys;

import java.util.ArrayList;

/**
 * @author Capitan on 11/7/17
 * @project OS_Simulator
 */
public class CPU implements Runnable{



    private int memory;     // The amount of memory available to a CPU
    private int cpuID;      // Figure we need an id to keep track of each CPU in case of multiple cores
    private PCB activeProcess; //The currently active process in the CPU
    private ArrayList<String> instructionSet; //The instruction set of the currently active process

    private boolean signalInterrupt = false; //Temp variable to use in the switch statement


    private Dispatcher dispatcher = Dispatcher.getInstance();


    private static final int BASE_MEMORY = 4096;
    private static final int BASE_CORES = 1;


    // Need to use the interrupt handler to determine when to swap processes
    // out/communicate with the CPU
    private final InterruptHandler interruptHandler;


    public CPU() {
        this(BASE_MEMORY, BASE_CORES, 1);
    }

    public CPU(int memory, int numCores, int cpuId) {
        this.interruptHandler = new InterruptHandler();
        this.memory = memory;
        this.cpuID = cpuId;
    }

    public void setActiveProcess() {
        this.activeProcess = dispatcher.getNextProcessToExecute();
    }


    /**
     * To work with the Runnable interface
     * TODO: COMPLETE THIS --> CURRENTLY DOES NOTHING OF VALUE
     * Need to increment clock on CPU while decrementing remaining burst time
     *
     */
    public void run() {
        String[] currentInstruction;

        if(this.activeProcess == null) {
            setActiveProcess();
        }
        instructionSet = this.activeProcess.getInstructions();
        while(!instructionSet.isEmpty() && !signalInterrupt ) {
            currentInstruction = instructionSet.remove(0).split(" ");
            switch(currentInstruction[0]){
                case "CALCULATE":
                    System.out.format("CALCULATE %d for pid : %d", currentInstruction[1], activeProcess.getPid());
                    break;
                case "I/O":
                    System.out.format("I/O %d for pid : %d", currentInstruction[1], activeProcess.getPid());
                    signalInterrupt = true;
                    break;
                case "YIELD":
                    System.out.format("YIELD %d for pid : %d", currentInstruction[1], activeProcess.getPid());
                    signalInterrupt = true;
                    break;
                case "OUT":
                    activeProcess.printPCBInfo();
                    break;
                default:
                    return;

            }
            // TODO: Actually make some logic here that does something
        }

    }


}
