package Sys;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author Will Russell on 11/8/17
 * @project OS_Simulator
 */
public class IODevice {

    private Queue<PCB> waitingQueue;

    public IODevice() {
        this.waitingQueue = new PriorityQueue<>();
    }

    public void executeIO() {
        PCB process = this.waitingQueue.poll();

    }

    public void addPCBToWaitingQueue(PCB process) {
        this.waitingQueue.add(process);
    }



}
