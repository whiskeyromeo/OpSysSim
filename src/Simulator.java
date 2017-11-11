import Sys.Kernel;
import Sys.PCB;
import Sys.Scheduling.FCFS;
import Sys.Scheduling.Scheduler;

/**
 * @author Capitan on 11/7/17
 * @project OS_Simulator
 */
public class Simulator {

    public static void main(String[] args) {

        // Fire up the kernel
        Kernel kernel = new Kernel();

        // Check that the base theory works
        System.out.println("check");
        System.out.println("System clock : " + kernel.getSystemClock());
        kernel.advanceClock();
        System.out.println("System clock : " + kernel.getSystemClock());
        PCB firstProcess = new PCB(kernel.getNewPid(), -1);

        System.out.println("Process " + firstProcess.getPid()  +  " arrival : " + firstProcess.getArrivalTime());

        kernel.advanceClock();
        kernel.advanceClock();

        PCB secondProcess = new PCB(kernel.getNewPid(), -1);

        FCFS fcfsScheduler = new FCFS();
        FCFS fcfsScheduler2 = new FCFS();

        fcfsScheduler.addToQueue(secondProcess);
        fcfsScheduler2.addToQueue(secondProcess);
        fcfsScheduler2.addToQueue(firstProcess);

        int val = fcfsScheduler.getQueue().size();
        int val2 = fcfsScheduler2.getQueue().size();

        PCB p = fcfsScheduler.getNextFromQueue();
        System.out.println("p : " + p.getPid());

        //Scheduler.Stats stats = fcfsScheduler.getStats();

        System.out.println("Process " + secondProcess.getPid()  +  " arrival : " + secondProcess.getArrivalTime());
        System.out.format("queue 1 size : %d\n queue 2 size : %d\n", val, val2);

        PCB firstChild = firstProcess._fork();

        System.out.format("First child : %d, parent: %d, arrival : %d, currentStatus : %s",
                firstChild.getPpid(), firstChild.getPpid(), firstChild.getArrivalTime(), firstChild.getCurrentState().toString());






    }

}
