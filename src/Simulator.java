import Sys.CPU;
import Sys.Kernel;
import Sys.Memory.MemoryManager;
import Sys.PCB;
import Sys.ProcessState;
import Sys.Scheduling.FCFS;
import Sys.Scheduling.LongTerm;
import Sys.Scheduling.MultiLevel;
import Sys.Scheduling.Scheduler;
import User_space.GUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
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
//        GUI gui = new GUI();
//        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        gui.setSize(600, 200);
//        gui.setVisible(true);
//        gui.setTitle("Processes Table");
//        runBaseTests();
        testCPU();

    }



    public static PCB makeFakeProcess(int mem) {
        PCB process = new PCB(kernel.getNewPid(), -1);
        ArrayList<String> commands = new ArrayList<>(Arrays.asList("CALCULATE 20", "YIELD", "I/O", "CALCULATE 10", "OUT", "CALCULATE 20"));
        process.initializeBlock(commands, 0, mem);
        return process;
    }

    public static void populateReadyQueues(int numProcesses) {
        LongTerm longTermScheduler = LongTerm.getInstance();
        Random r = new Random();
        int mem;
        for(int i=0; i < numProcesses; i++){
            mem = r.nextInt(300);
            PCB process = makeFakeProcess(mem);
            longTermScheduler.addToWaitingQueue(process);
        }
        for(int i=0; i < numProcesses; i++){
            longTermScheduler.scheduleWaitingProcess();
        }

    }


    public static void testCPU() {
        populateReadyQueues(4);
        CPU cpu = new CPU(1);
        cpu.run();

    }


    public static void runBaseTests() {
        System.out.println("-----TESTING MEMORY MANAGER ------");
        MemoryManager memoryManager = MemoryManager.getInstance();
        System.out.format("Current memory : %s\n", memoryManager.getCurrentMemory());


        System.out.println("----TESTING PROCESS CREATION");
        PCB firstProcess = makeFakeProcess(30);
        System.out.println("Process " + firstProcess.getPid()  +  " arrival : " + firstProcess.getArrivalTime());
        kernel.advanceClock();
        PCB secondProcess = makeFakeProcess(220);
        System.out.println("Process " + secondProcess.getPid()  +  " arrival : " + secondProcess.getArrivalTime());


        System.out.println("-----TESTING LONG TERM SCHEDULER------");
        LongTerm longTermScheduler = LongTerm.getInstance();
        longTermScheduler.addToWaitingQueue(secondProcess);
        longTermScheduler.addToWaitingQueue(firstProcess);
        longTermScheduler.scheduleWaitingProcess();
        longTermScheduler.scheduleWaitingProcess();
        System.out.format("Current memory : %s\n", memoryManager.getCurrentMemory());

        System.out.println("-----TESTING MultiLevel SCHEDULER------");
        MultiLevel multiLevel = MultiLevel.getInstance();
        PCB next = multiLevel.getNextProcess();
        System.out.format("next id : %d\n", next.getPid());
        next = multiLevel.getNextProcess();
        System.out.format("2nd next id : %d\n", next.getPid());

        System.out.println("-----TESTING PROCESS STATES and FORK-----");
        firstProcess.setCurrentState(ProcessState.STATE.RUN);
        System.out.format("First process : %d, parent: %d, arrival : %d, currentStatus : %s\n",
                firstProcess.getPid(), firstProcess.getPpid(), firstProcess.getArrivalTime(), firstProcess.getCurrentState().toString());
        PCB firstChild = firstProcess._fork();
        System.out.format("First process : %d, parent: %d, arrival : %d, currentStatus : %s\n",
                firstProcess.getPid(), firstProcess.getPpid(), firstProcess.getArrivalTime(), firstProcess.getCurrentState().toString());
        System.out.format("First child : %d, parent: %d, arrival : %d, currentStatus : %s\n",
                firstChild.getPid(), firstChild.getPpid(), firstChild.getArrivalTime(), firstChild.getCurrentState().toString());
        next = multiLevel.getNextProcess();
        // Checking that fork works properly
        System.out.format("3rd next id : %d\n", next.getPid());

    }



}
