package Sys.Scheduling;

import Sys.PCB;

import java.util.ArrayList;

/**
 * @project OS_Simulator
 */
public class RoundRobin {
    private final int TIME_QUANTUM = 10;
    private volatile static ArrayList<PCB> queue = new ArrayList<>();

    private static RoundRobin rrobinScheduler;

    private RoundRobin() {}

    public static RoundRobin getInstance() {
        if(rrobinScheduler == null) {
            rrobinScheduler = new RoundRobin();
        }
        return rrobinScheduler;
    }




}
