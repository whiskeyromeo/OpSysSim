package Sys;

import Sys.Memory.Register;
import Sys.ProcessState.STATE;
import Sys.Scheduling.MultiLevel;

import java.util.ArrayList;

/**
 *
 * @project OS_Simulator
 */
public class PCB implements Cloneable {

    Dispatcher dispatcher = Dispatcher.getInstance();
    MultiLevel multiLevelScheduler = MultiLevel.getInstance();

    private STATE currentState;
    private int pid;                // unique process identifier for each process
    private int ppid;               // parent process id
    private int programCounter;     // should point to the location of the next process to be executed in the program file
    private int arrivalTime;        // arrival time of the process
    private int burstTime;
    private int waitTime;           // current wait time of the process
    private int memRequired;        // amount of memory required from the program file
    private int memAllocated;       // amount of memory currently allocated to the process
    private int ioRequests;
    private int estimatedRunTime;
    private int nextBurst;


    private ArrayList<String> instructions; // set of instructions to be executed from the file
    private ArrayList<PCB> children; // list of children of the process
    private ArrayList<Register> registers; // Set of registers needed by the process

    public PCB(int id, int parentId) {
        this.pid = id;                                  // id of the process
        this.ppid = parentId;                           // parent id if exists, otherwise -1
        this.registers = new ArrayList<>();     // The set of registers allocated to the process
        this.instructions = new ArrayList<>();    // The set of instructions for the process parsed from a file
        this.children = new ArrayList<>();           // children of the process
        this.arrivalTime = Kernel.getSystemClock();
        this.currentState = STATE.NEW;
        this.waitTime = 0;
        this.burstTime = 0;
        this.memAllocated = 0;
        this.ioRequests = 0;
        this.programCounter = 0; // program counter --> points to next process to be executed in program file
        this.estimatedRunTime = 0;
    }

    public PCB(int id, int parentId, ArrayList<Register> registers, ArrayList<String> instructions, int cycles, STATE state, int pc, int parentRunTime, int nextBurst) {
        this.pid = id;
        this.ppid = parentId;
        this.registers = registers;
        this.instructions = instructions;
        this.children = new ArrayList<PCB>();
        this.arrivalTime = Kernel.getSystemClock();
        this.currentState = state;
        this.waitTime = 0;
        this.programCounter = pc;
        this.burstTime = cycles;
        this.ioRequests = 0;
        this.estimatedRunTime = parentRunTime;
        this.nextBurst = nextBurst;

    }



    /**
     * forks a process --> returns the parent to the ReadyQueue
     * @return The child process
     */
    public PCB _fork() throws IllegalArgumentException{
        if(this.currentState != STATE.RUN) {
            throw new IllegalArgumentException("The process must be RUNNING to be forked");
        }
        // Copy the parent's information into the child
        PCB child = new PCB(
                Kernel.getNewPid(),
                this.pid,
                this.registers,
                this.instructions,
                this.burstTime,
                STATE.RUN,
                this.programCounter,
                this.estimatedRunTime,
                this.nextBurst
        );

        this.children.add(child);
        this.setCurrentState(STATE.READY);
        multiLevelScheduler.scheduleProcess(this);
        return child;
    }


    public int _exec() {
        // Should execute in place of the current shell without creating a new process
        return 0;
    }

    public int _wait(int process_id) {
        // Need to have some sort of status check to see if the process passed in has completed

        return 0;
    }

    public STATE _exit() {
        this.currentState = STATE.EXIT;
        return this.currentState;
    }

    // ***** GETTERS ******
    public STATE getCurrentState() { return this.currentState; }
    public int getArrivalTime() { return this.arrivalTime; }
    public int getPid() { return this.pid; }
    public int getPpid() { return this.ppid; }
    public int getMemRequired() { return this.memRequired; }
    public int getProgramCounter() { return this.programCounter; }
    public int getMemAllocated() { return this.memAllocated; }
    public int getEstimatedRunTime() { return this.estimatedRunTime; }
    public int getNextBurst() { return this.nextBurst; }

    // TODO: REMOVE WHEN CPU IS FUNCTIONAL
    public int getBurstTime() {
        return this.burstTime;
    }

    public ArrayList<String> getInstructions() { return instructions; }
    public ArrayList<PCB> getChildren() { return children; }
    public ArrayList<Register> getRegisters() { return registers; }

    // ******* SETTERS *******
    public void setCurrentState(STATE newState) { this.currentState = newState; }
    public void setArrivalTime( int arrival) { this.arrivalTime = arrival; }
    public void setMemRequired(int mem) { this.memRequired = mem; }
    public void setProgramCounter(int pc) { this.programCounter = pc; }
    public void setBurstTime(int burstTime) { this.burstTime = burstTime; }
    public void setMemAllocated(int mem) { this.memAllocated = mem; }
    public void setNextBurst(int burst) { this.nextBurst = burst; }

    public void decrementEstimatedRunTime(int mem) { this.estimatedRunTime -= mem; }

    /**
     * Initialize a PCB with the relevant information
     * @param commands --> An ArrayList of the String commands from a file
     * @param instructionIndex --> The index of the next instruction to execute in the ArrayList
     * @param memNeeded --> memory required by the process
     */
    public void initializeBlock(ArrayList<String> commands, int instructionIndex, int memNeeded, int estCycles ) {
        this.instructions = commands;
        this.programCounter = instructionIndex;
        this.memRequired = memNeeded;
        this.estimatedRunTime = estCycles;
    }

    public void exit() {
        this.currentState = STATE.EXIT;
        this.burstTime = 0;
        this.memRequired = 0;
        this.programCounter = 0;
        this.registers = null;
    }

    /**
     * Method to decrement burst time
     */
    public void incrementBurstTime() {
        this.burstTime++;
    }


    /**
     * Prints out some details about the PCB
     */
    public void printPCBInfo() {
        System.out.println("PID : " + this.pid +
                "\nPPID : " + this.ppid +
                "\nCurrent Instruction : " + this.instructions.get(this.programCounter)
        );
    }


}
