package Sys;

import java.util.ArrayList;

/**
 * @project OS_Simulator
 */
public class RunningQueue {
    public static ArrayList<PCB> runningList = new ArrayList<>();


    public static void addToList(PCB process) {
        runningList.add(process);
    }

    public synchronized static void removeFromList(PCB process) {
        while(runningList.contains(process)) {
            runningList.remove(process);
        }
    }

    public static int getSize() {
        return runningList.size();
    }
}
