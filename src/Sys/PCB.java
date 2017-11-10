package Sys;

import Sys.ProcessState.STATE;

import java.util.ArrayList;

/**
 * @author Will Russell on 11/7/17
 * @project OS_Simulator
 */
public class PCB {

    private STATE currentState;
    private int pid;                // unique process identifier for each process
    private int ppid;               // parent process id
    private int programCounter;     // should point to the location of the next process to be executed in the program file
    private int priorityNum;        // associated value for priority scheduling
    private int arrivalTime;        // arrival time of the process
    private int waitTime;           // current wait time of the process
    private int burstAmount;        // burst amount from the program file
    private int memRequired;        // amount of memory required from the program file

    private ArrayList<PCB> children; // list of children of the process



    public PCB(int id, int parentId) {
        this.pid = id;                                  // id of the process
        this.ppid = parentId;                           // parent id if exists, otherwise -1
        this.priorityNum = 0;                           // priority number of the process
        this.programCounter = 0;                        // program counter --> points to next process to be executed in program file
        this.children = new ArrayList<PCB>();           // children of the process
        this.arrivalTime = Kernel.getSystemClock();     //
        this.currentState = STATE.NEW;


    }

    /**
     * Since we don't have pointers in java, need to find some way to reference the child in the fork;
     * TODO: Maybe revisit this and use the ExecutorClass interface or some type of threading to simulate forking?
     *
     * @return
     */
    public PCB _fork() {
        PCB child = new PCB(Kernel.getNewPid(), this.pid);
        this.children.add(child);
        return child;
    }

    public int _exec() {

        return 0;
    }

    public int _wait() {

        return 0;
    }

    public int _exit() {

        return 0;
    }

    // ***** GETTERS ******
    public STATE getCurrentState() { return this.currentState; }
    public int getArrivalTime() { return this.arrivalTime; }
    public int getPid() { return this.pid; }
    public int getPpid() { return this.ppid; }
    public int getPriorityNum() { return this.priorityNum; }
    public int getWaitTime() { return this.waitTime; }
    public int getBurstAmount() { return this.burstAmount; }
    public int getMemRequired() { return this.memRequired; }

    // ******* SETTERS *******
    public void setCurrentState(STATE newState) { this.currentState = newState; }
    public void setPriorityNum(int priority) { this.priorityNum = priority; }
    public void setWaitTime(int wait) { this.waitTime = wait; }
    public void setBurstAmount(int burst) { this.burstAmount = burst; }
    public void setMemRequired(int mem) { this.memRequired = mem; }

}
