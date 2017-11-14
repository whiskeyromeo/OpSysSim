import Sys.Kernel;
import Sys.PCB;
import Sys.ProcessState;
import Sys.Scheduling.FCFS;
import Sys.Scheduling.Scheduler;
import User_space.GUI;
import java.awt.*;
import javax.swing.*;

/**
 * @author Capitan on 11/7/17
 * @project OS_Simulator
 */
public class Simulator {

    // Fire up the kernel
    static Kernel kernel = new Kernel();


    public static void main(String[] args)
    {
        GUI gui = new GUI();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setSize(600, 200);
        gui.setVisible(true);
        gui.setTitle("Processes Table");
        runBaseTests();

    }

    public static void runBaseTests() {
        // Check that the base theory works
        System.out.println("----TESTING SYSTEM CLOCK------");
        System.out.println("System clock : " + kernel.getSystemClock());
        kernel.advanceClock();
        kernel.advanceClock();
        System.out.println("System clock : " + kernel.getSystemClock());

        System.out.println("----TESTING PROCESS CREATION");
        PCB firstProcess = new PCB(kernel.getNewPid(), -1);
        System.out.println("Process " + firstProcess.getPid()  +  " arrival : " + firstProcess.getArrivalTime());
        kernel.advanceClock();
        PCB secondProcess = new PCB(kernel.getNewPid(), -1);
        System.out.println("Process " + secondProcess.getPid()  +  " arrival : " + secondProcess.getArrivalTime());


        System.out.println("-----TESTING FCFS SCHEDULER------");
        FCFS fcfsScheduler = new FCFS();
        FCFS fcfsScheduler2 = new FCFS();
        fcfsScheduler.addToQueue(secondProcess);
        fcfsScheduler2.addToQueue(secondProcess);
        fcfsScheduler2.addToQueue(firstProcess);
        System.out.format("FCFS 1 size : %d\n", fcfsScheduler.getQueue().size());
        System.out.format("FCFS 2 size : %d\n", fcfsScheduler2.getQueue().size());


        System.out.println("-----TESTING PROCESS STATES and FORK-----");
        firstProcess.setCurrentState(ProcessState.STATE.RUN);
        System.out.format("First process : %d, parent: %d, arrival : %d, currentStatus : %s\n",
                firstProcess.getPid(), firstProcess.getPpid(), firstProcess.getArrivalTime(), firstProcess.getCurrentState().toString());
        PCB firstChild = firstProcess._fork();
        System.out.format("First process : %d, parent: %d, arrival : %d, currentStatus : %s\n",
                firstProcess.getPid(), firstProcess.getPpid(), firstProcess.getArrivalTime(), firstProcess.getCurrentState().toString());
        System.out.format("First child : %d, parent: %d, arrival : %d, currentStatus : %s\n",
                firstChild.getPid(), firstChild.getPpid(), firstChild.getArrivalTime(), firstChild.getCurrentState().toString());


    }

}
