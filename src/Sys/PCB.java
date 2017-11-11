package Sys;

import Sys.Memory.Register;
import Sys.ProcessState.STATE;

import java.util.ArrayList;

/**
 * @author Will Russell on 11/7/17
 * @project OS_Simulator
 */
public class PCB {

    Dispatcher dispatcher = Dispatcher.getInstance();

    private STATE currentState;
    private int pid;                // unique process identifier for each process
    private int ppid;               // parent process id
    private int programCounter;     // should point to the location of the next process to be executed in the program file
    private int priorityNum;        // associated value for priority scheduling
    private int arrivalTime;        // arrival time of the process
    private int waitTime;           // current wait time of the process
    private int initialBurst; // burst amount from the program --> should not be updated
    private int remainingBurst;        // burst amount from the program file
    private int nextBurst;        //
    private int memRequired;        // amount of memory required from the program file
    private int memAllocated;
    private boolean privelege;      // true = access kernel resources, false = no access to kernel resources
    private boolean ioBound;      //  is process

    private ArrayList<String> instructions; // set of instructions to be executed from the file
    private ArrayList<PCB> children; // list of children of the process
    private ArrayList<Register> registers; // Set of registers needed by the process

    public PCB(int id, int parentId) {
        this.pid = id;                                  // id of the process
        this.ppid = parentId;                           // parent id if exists, otherwise -1
        this.registers = new ArrayList<Register>();
        this.instructions = new ArrayList<String>();    // The set of instructions for the process -> remove instruction from head upon completed execution
        this.children = new ArrayList<PCB>();           // children of the process
        this.arrivalTime = Kernel.getSystemClock();
        this.currentState = STATE.NEW;
        this.privelege = false;
        this.waitTime = 0;
        this.priorityNum = 0;                           // priority number of the process
        this.memAllocated = 0;
        this.programCounter = 0;                        // program counter --> points to next process to be executed in program file

    }

    /**
     * forks a process --> returns the parent to the ReadyQueue
     * @return The child process
     */
    public PCB _fork() throws IllegalArgumentException{
        if(this.currentState != STATE.RUN) {
            throw new IllegalArgumentException("The process must be RUNNING to be forked");
        }
        PCB child = new PCB(Kernel.getNewPid(), this.pid);
        this.children.add(child);
        dispatcher.addProcessToReadyQueue(this);
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
    public int getPriorityNum() { return this.priorityNum; }
    public int getWaitTime() { return this.waitTime; }
    public int getRemainingBurst() { return this.remainingBurst; }
    public int getNextBurst() { return this.nextBurst; }
    public int getMemRequired() { return this.memRequired; }
    public int getMemAllocated() { return this.memAllocated; }
    public boolean getPrivelege() { return this.privelege; }
    public boolean isIoBound() { return ioBound; }

    // ******* SETTERS *******
    public void setCurrentState(STATE newState) { this.currentState = newState; }
    public void setPriorityNum(int priority) { this.priorityNum = priority; }
    public void setWaitTime(int wait) { this.waitTime = wait; }
    public void setRemainingBurst(int burst) { this.remainingBurst = burst; }
    public void setNextBurst(int burst) { this.nextBurst = burst; }
    public void setMemRequired(int mem) { this.memRequired = mem; }
    public void setPrivelege(boolean status) { this.privelege = status; }
    public void setIoBound(boolean status) { this.ioBound = true; }
    public void setInitialBurst(int burst) { this.initialBurst = burst; }

    public void initializeBlock(int burst, ArrayList<String> commands, int instructionIndex, int memNeeded ) {
        this.initialBurst = burst;
        this.remainingBurst = burst;
        this.instructions = commands;
        this.programCounter = instructionIndex;
        this.memRequired = memNeeded;
    }


}
