package Sys;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @project OS_Simulator
 */
public class RunningQueue {
    public static CopyOnWriteArrayList<PCB> runningList = new CopyOnWriteArrayList<>();


    public synchronized static void addToList(PCB process) {
        runningList.add(process);
    }

    public synchronized static void removeFromList(PCB process) {
        while(runningList.contains(process)) {
            runningList.remove(process);
        }
    }

    public synchronized static int getSize() {
        return runningList.size();
    }
}
