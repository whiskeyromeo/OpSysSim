package Sys;

import User_space.GUI;

import java.util.ArrayList;

/**
 * @project OS_Simulator
 */
public class CPU {

    private final int CORE_COUNT = 2;
    private boolean coresInitialized = false;
    ArrayList<Thread> cores = new ArrayList<>();
    int id;

    public Core core1;
    public Core core2;

    Thread c1;
    Thread c2;

    public CPU(int id) {
        this.id = id;
        this.core1 = new Core(1);
        this.core2 = new Core(2);
    }


    public void initializeCPUThreads() {
        c1 = new Thread() {
            public void run() {
                core1.run();
            }
        };

        c2 = new Thread() {
            public void run() {
                core2.run();
            }
        };
        coresInitialized = true;
    }



    public void runCPUThreads() {
        c1.run();
        c2.run();
    }

    public void startCPUThreads() {
        c1.start();
        c2.start();
    }


    public void run() {
        if(GUI.isActive) {
            if(!coresInitialized) {
                initializeCPUThreads();
            }
            runCPUThreads();
        } else {
            if(!coresInitialized) {
                initializeCPUThreads();
                startCPUThreads();
            }
        }
    }

    public int getRunningProcesses() {
        int procNum = 0;
        procNum += core1.getRunningCount();
        procNum += core2.getRunningCount();

        return procNum;
    }


}
