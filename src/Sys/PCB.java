package Sys;

import Sys.ProcessState.STATE;

import java.util.ArrayList;

/**
 * @author Will Russell on 11/7/17
 * @project OS_Simulator
 */
public class PCB {

    private STATE currentState;
    private boolean isActive;       // state of the process
    private int pid;                // unique process identifier for each process
    private int ppid;               // parent process id
    private ArrayList<PCB> children;

    public PCB(int id, int parentId) {
        this.pid = id;
        this.ppid = parentId;
    }

    public int _fork() {

        return 0;
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



    public void setCurrentState(STATE newState) {
        this.currentState = newState;
    }

    public STATE getCurrentState() {
        return this.currentState;
    }
}
