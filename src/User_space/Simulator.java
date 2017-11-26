package User_space;

import Sys.CPU;
import Sys.Kernel;
import Sys.Memory.MemoryManager;
import Sys.PCB;
import Sys.ProcessState;
import Sys.Scheduling.LongTerm;
import Sys.Scheduling.MultiLevel;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Capitan on 11/7/17
 * @project OS_Simulator
 */
public class Simulator {

    // Fire up the kernel
    static Kernel kernel = new Kernel();
    private static LongTerm longTermScheduler = LongTerm.getInstance();


    //public Simulator() {}
    private static Simulator simulator;

    protected Simulator() { }

    public static Simulator getInstance() {
        if(simulator == null) {
            simulator = new Simulator();
        }
        return simulator;
    }


//    public static void main(String[] args)
//    {
//        GUI_v2 gui = new GUI_v2();
//        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        gui.setSize(600, 200);
//        gui.setVisible(true);
//        gui.setTitle("Processes Table");
////        runBaseTests();
//        //testCPU();
//
//    }

    /**
     * Hacky method to get the estimated number of cycles for printing later
     * @param commands
     * @return
     */
    public static int calculateEstimatedCycles(ArrayList<String> commands) {
        int cyclesNeeded = 0;
        for(String command: commands) {
            String val = command.replaceAll("[^0-9]","");
            if(val.length() > 0) {
                cyclesNeeded += Integer.valueOf(val);
            } else if(command.matches("I/O")) {
                cyclesNeeded += 50; // Assume the max
            } else {
                cyclesNeeded += 1; // Assume all other commands take 1 cycle
            }

        }
        return cyclesNeeded;
    }

    //******** BEGIN FILE PROCESS GENERATION METHODS ********************//

    public static PCB prepareProcessForSimulation(ArrayList<String> fileCommands) {
        PCB process = new PCB(kernel.getNewPid(), -1);
        int mem = Integer.parseInt(fileCommands.remove(0));
        int cycles = calculateEstimatedCycles(fileCommands);
        process.initializeBlock(fileCommands, 0, mem, cycles);
        return process;
    }


    public static void scheduleProcessFromFile(ArrayList<String> fileCommands) {
        PCB process = prepareProcessForSimulation(fileCommands);
        longTermScheduler.addToWaitingQueue(process);
    }

    //******** END FILE PROCESS GENERATION METHODS ********************//

    //******** BEGIN FAKE PROCESS GENERATION METHODS ********************//


    public static ArrayList<String> generateFakeProcessString(int length) {
        int chosen, randCalcVal;
        String[] commands = {"CALCULATE", "YIELD","I/O", "OUT"};
        ArrayList<String> commandList = new ArrayList<>();
        Random r = new Random();
        for(int i = 0; i < length; i++) {
            chosen = r.nextInt(4);
            if(chosen == 0) {
                randCalcVal = r.nextInt(200) + 1;
                commandList.add(commands[chosen] + " " + String.valueOf(randCalcVal));
            } else {
                commandList.add(commands[chosen]);
            }
        }
        return commandList;

    }


    public static PCB makeFakeProcess(int mem, int processLen) {
        PCB process = new PCB(kernel.getNewPid(), -1);
        ArrayList<String> commands = generateFakeProcessString(processLen);
        int cycles = calculateEstimatedCycles(commands);
        process.initializeBlock(commands, 0, mem, cycles);
        return process;
    }

    public static void populateReadyQueues(int numProcesses) {
        Random r = new Random();
        int mem, procLen;
        for(int i=0; i < numProcesses; i++){
            mem = r.nextInt(300);
            procLen = r.nextInt(14) + 1;
            PCB process = makeFakeProcess(mem,procLen);
            longTermScheduler.addToWaitingQueue(process);
        }
        for(int i=0; i < numProcesses; i++){
            longTermScheduler.scheduleWaitingProcess();
        }

    }

    public static void populateReadyQueues(int numProcesses, int mem) {
        Random r = new Random();
        int procLen;
        for(int i=0; i < numProcesses; i++){
            procLen = r.nextInt(14) + 1;
            PCB process = makeFakeProcess(mem,procLen);
            longTermScheduler.addToWaitingQueue(process);
        }
        for(int i=0; i < numProcesses; i++){
            longTermScheduler.scheduleWaitingProcess();
        }

    }

    //******** END FAKE PROCESS GENERATION METHODS ********************//

    public static void testCPU() {
        populateReadyQueues(5);
        CPU cpu = new CPU(1);
        cpu.run();

    }



    public static void retrieveProcessInfo(PCB process) {
        System.out.format("next id : %d\n", process.getPid());
        System.out.format("estimated : %d\n", process.getEstimatedRunTime());
        printInstructionSet(process);
    }

    public static void printInstructionSet(PCB process) {
        System.out.format("instructions : ");
        for(String instruction : process.getInstructions()) {
            System.out.format("%s ,", instruction);
        }
        System.out.format("\n");
    }




    public static void runBaseTests() {
        System.out.println("-----TESTING MEMORY MANAGER ------");
        MemoryManager memoryManager = MemoryManager.getInstance();
        System.out.format("Current memory : %s\n", memoryManager.getCurrentMemory());


        System.out.println("----TESTING PROCESS CREATION");
        PCB firstProcess = makeFakeProcess(30, 8);
        System.out.println("Process " + firstProcess.getPid()  +  " arrival : " + firstProcess.getArrivalTime());
        kernel.advanceClock();
        PCB secondProcess = makeFakeProcess(220, 8);
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
