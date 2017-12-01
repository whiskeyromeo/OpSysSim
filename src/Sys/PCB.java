package Sys;

import Sys.Memory.MemoryManager;
import Sys.Memory.Register;
import Sys.ProcessState.STATE;
import Sys.Scheduling.MultiLevel;
import User_space.Simulator;

import java.util.ArrayList;

/**
 *
 * @project OS_Simulator
 */
public class PCB implements Cloneable {

    Kernel kernel = Kernel.getInstance();
    Dispatcher dispatcher = Dispatcher.getInstance();
    MultiLevel multiLevelScheduler = MultiLevel.getInstance();
    MemoryManager memoryManager = MemoryManager.getInstance();
    Simulator simulator = Simulator.getInstance();

    private STATE currentState;
    private int pid;                // unique process identifier for each process
    private int ppid;               // parent process id
    private int programCounter;     // should point to the location of the next process to be executed in the program file
    private int arrivalTime;        // arrival time of the process
    private int burstTime;
    private int criticalTime;      // Arrival time to the io queue
    private int memRequired;        // amount of memory required from the program file
    private int memAllocated;       // amount of memory currently allocated to the process
    private int ioRequests;
    private int loadTime;
    private CommObject sharedString;

    private int estimatedRunTime;
    private int nextBurst;
    private int completedTime;      // Time the process exits


    private ArrayList<String> instructions; // set of instructions to be executed from the file
    private ArrayList<Integer> children; // list of children of the process
    private ArrayList<Register> registers; // Set of registers needed by the process

    public PCB(int id, int parentId, int clockTime) {
        this.pid = id;                                  // id of the process
        this.ppid = parentId;                           // parent id if exists, otherwise -1
        this.registers = new ArrayList<>();     // The set of registers allocated to the process
        this.instructions = new ArrayList<>();    // The set of instructions for the process parsed from a file
        this.children = new ArrayList<>();           // children of the process
        this.arrivalTime = clockTime;
        this.currentState = STATE.NEW;
        this.criticalTime = 0;
        this.burstTime = 0;
        this.memAllocated = 0;
        this.ioRequests = 0;
        this.programCounter = 0; // program counter --> points to next process to be executed in program file
        this.estimatedRunTime = 0;
        this.completedTime = -1;
    }

    public PCB(int id, int parentId, ArrayList<Register> registers, ArrayList<String> instructions, int cycles, STATE state, int pc, int nextBurst) {
        this.pid = id;
        this.ppid = parentId;
        this.registers = registers;
        this.instructions = instructions;
        this.children = new ArrayList<Integer>();
        this.arrivalTime = kernel.getSystemClock();
        this.currentState = state;
        this.criticalTime = 0;
        this.programCounter = pc;
        this.burstTime = cycles;
        this.ioRequests = 0;
        this.nextBurst = nextBurst;

    }


    /**
     * forks a process --> returns the parent to the ReadyQueue
     * @return The child process
     */
    public synchronized PCB _fork() throws IllegalArgumentException{
        if(this.currentState != STATE.RUN) {
            throw new IllegalArgumentException("The process must be RUNNING to be forked");
        }
        // Copy the parent's information into the child
        PCB child = new PCB(
                Kernel.getNewPid(),
                this.pid,
                this.registers,
                simulator.generateFakeProcessString(3),
                this.burstTime,
                STATE.RUN,
                0,
                this.burstTime
        );
        if(memoryManager.getCurrentMemory() > this.memRequired/2){
            memoryManager.allocateMemory(this.memRequired/2);
            child.memAllocated = this.memRequired/2;
            child.memRequired = this.memRequired/2;

        } else {
            child = null;
            return this;
        }
        child.estimatedRunTime = simulator.calculateEstimatedCycles(child.instructions);
        child.sharedString = this.sharedString;
        this.children.add(child.getPid());
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
    public int getIoRequests( ) { return this.ioRequests; }
    public String getSharedString() { return this.sharedString.getSharedPiece(); }

    // TODO: REMOVE WHEN CPU IS FUNCTIONAL
    public int getBurstTime() {
        return this.burstTime;
    }

    public ArrayList<String> getInstructions() { return instructions; }
    public ArrayList<Integer> getChildren() { return children; }
    public ArrayList<Register> getRegisters() { return registers; }

    // ******* SETTERS *******
    public void setCurrentState(STATE newState) { this.currentState = newState; }
    public void setArrivalTime( int arrival) { this.arrivalTime = arrival; }
    public void setProgramCounter(int pc) { this.programCounter = pc; }
    public void setBurstTime(int burstTime) { this.burstTime = burstTime; }
    public void setMemAllocated(int mem) { this.memAllocated = mem; }
    public void setNextBurst(int burst) { this.nextBurst = burst; }
    public void setLoadTime(int load) { this.loadTime = load; }
    public void addToSharedString(int id) { this.sharedString.setSharedPiece(id); }
    public void incrementIoRequests() { this.ioRequests++; }
    public void incrementCriticalTime(int amount) { this.criticalTime += amount; }
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
        this.sharedString = new CommObject(this.pid);
    }

    public void exit() {
        this.currentState = STATE.EXIT;
        this.memAllocated = 0;
        this.completedTime = Kernel.getStaticSystemClock();
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
                "\nCurrent Instruction : " + this.instructions.get(this.programCounter) +
                "\nCritical Time : " + this.criticalTime
        );
    }

    public String sanitizePCString() {
        if(this.programCounter >= this.instructions.size()) {
            return "--ending...";
        } else {
            return this.instructions.get(this.programCounter);
        }
    }

    /**
     * @return The string printed when a job is in the waiting queue
     */
    public String getNewPCBLine() {
        return "PID : " + this.pid +
                " - State : " + this.currentState +
                " - Est Cyc : " + this.estimatedRunTime +
                " - Mem : " + this.memRequired +
                " - Exec : " + sanitizePCString() +
                " - Arr time : " + this.arrivalTime + "";
    }

    /**
     * @return The string printed when a process finishes or PROC is called
     */
    public String getPCBLine() {
        String output = "PID : " + this.pid +
                " - State : " + this.currentState +
                " - IO Req : " + this.ioRequests +
                " - Mem : " + this.memAllocated +
                " - Next : " + sanitizePCString() +
                " - Load time : " + this.loadTime + "";
        if(this.completedTime > -1) {
            output += " - Comp Time : " + this.completedTime;
        }
        return output;
    }

    /**
     * @return The string printed when a process calls OUT
     */
    public String getPCBOutput() {
        String output = "PID : " + this.pid +
                "\n\t PPID : " + this.ppid +
                "\n\t Next Instruction : " + this.instructions.get(this.programCounter) +
                "\n\t Next Burst : " + this.nextBurst +
                "\n\t Load Time : " + this.loadTime +
                "\n\t Last Scheduled Time : " + this.arrivalTime +
                "\n\t State : " + this.currentState +
                "\n\t IO Requests : " + this.ioRequests +
                "\n\t Critical Time : " + this.criticalTime +
                "\n\t Instruction set : " + String.join(", ", instructions);
        if(this.children.size() > 0)
            output += "\n\t Children : " + String.join(", ", this.children.toString());

        return output;
    }


}
