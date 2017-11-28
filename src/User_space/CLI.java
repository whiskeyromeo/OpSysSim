package User_space;


import Sys.CPU;
import Sys.InterruptHandler;
import Sys.Kernel;
import Sys.Memory.MemoryManager;
import Sys.PCB;
import Sys.Scheduling.IOScheduler;
import Sys.Scheduling.LongTerm;
import Sys.Scheduling.MultiLevel;

import java.util.ArrayList;

/**
 * @project OS_Simulator
 */
public class CLI extends GUI {


    private static MultiLevel multiLevel = MultiLevel.getInstance();
    private static MemoryManager memoryManager = MemoryManager.getInstance();
    private static Kernel kernel = Kernel.getInstance();
    private static Simulator simulator = Simulator.getInstance();
    private static IOScheduler ioScheduler = IOScheduler.getInstance();
    private static LongTerm longTerm = LongTerm.getInstance();


    public static String[] commands = {"PROC", "MEM", "EXE", "RESET", "EXIT", "LOAD"};

    public static FileParser fileParser = new FileParser();


    public static boolean runProgramContinuously = false;
    public static int numExeSteps = -1;



    public static boolean isValidCommand(String input) {
        String[] command = input.toUpperCase().split(" ");
        for(String c: command) {
            System.out.println("C is : " + c);
        }
        if(command.length > 2) {
            return false;
        }
        boolean valid = false;
        for(String c: commands) {
            if(c.equalsIgnoreCase(command[0])) {
                if (command.length == 1 && !c.equals("LOAD")) {
                    valid = true;
                } else if(c.equalsIgnoreCase("LOAD")) {
                    if(command.length == 2) {
                        valid = true;
                    }
                } else {
                    String val = command[1].replaceAll("[^0-9]","");
                    System.out.println("Val = " + val);
                    if(val.length() >= 1) {
                        valid = true;
                    }
                }
            }
        }
        return valid;
    }


    public static void execute(String input) {
        String[]  command = input.split(" ");
        command[0] = command[0].toUpperCase();
//
//        for(String c: command) {
//            System.out.println("--command is : " + c);
//        }
        int val = -1;
        if(command.length > 1) {
            if(!command[0].equals("LOAD")) {
                val = Integer.parseInt(command[1]);
            }
        }
        GUI.addLine("-- executing --> " + String.join(" ",command));
        switch(command[0]) {
            case "PROC":
                _proc();
                break;
            case "MEM":
                _mem();
                break;
            case "EXE":
                _exe(val);
                break;
            case "LOAD":
                _load(command[1]);
                break;
            case "RESET":
                _reset();
                break;
            case "EXIT":
                _exit();
                break;
            default:
                System.out.println("----INVALID COMMAND FROM CLI----");
                break;
        }
    }

    public static void _proc() {
        ArrayList<PCB> processQueue = multiLevel.getReadyQueue();
        if(processQueue.isEmpty()){
            GUI.addLine("No processes running...");
        } else {
            for(PCB process : processQueue) {
                String output = String.format("Process %d :: State : %s, Estimated Run Time : %d, IO : %d",
                        process.getPid(), process.getCurrentState(), process.getEstimatedRunTime(), process.getIoRequests());
                GUI.addLine(output);
            }
        }
    }

    public static void _mem() {
        GUI.addLine(String.format("Memory Remaining : %d", memoryManager.getCurrentMemory()));
    }


    public static void _exe(int cycles) {
        if(cycles == -1) {
            // EXECUTE CONTINUOUSLY
            if(!InterruptHandler.interruptSignalled) {
                simulator.populateReadyQueues(5);
            }
            InterruptHandler.interruptSignalled = false;
            numExeSteps = -1;
            runProgramContinuously = true;

        } else {
            if(InterruptHandler.interruptSignalled) {
                InterruptHandler.interruptSignalled = false;
            }
            // EXECUTE FOR THE GIVEN NUMBER OF CYCLES
            numExeSteps = cycles;
        }

    }

    public static void _load(String filename) {
        fileParser.parse(filename);
        ArrayList<String> commands = fileParser.getCommandQueue();
        if(commands.isEmpty()) {
           GUI.addLine("There was an error loading the file");
           return;
        }
        if(filename.contains(".job")) {
            GUI.addLine("---> Loaded " + filename);
            int mem = Integer.valueOf(commands.remove(0));
            commands.remove(commands.size()-1);
            PCB process = new PCB(kernel.getNewPid(), -1, kernel.getSystemClock());
            int estimatedCycles = simulator.calculateEstimatedCycles(commands);
            process.initializeBlock(commands, 0, mem, estimatedCycles);
            longTerm.addToWaitingQueue(process);
            longTerm.scheduleWaitingProcess();
        } else if(filename.contains(".pgrm")) {
            executeProgramCommands(commands);
        } else {
            GUI.addLine("Name extension not recognized");
        }
    }

    public static void executeProgramCommands(ArrayList<String> commands) {
        while(commands.size() > 0) {

        }
    }

    public static void _reset() {
        memoryManager.resetMemory();
        longTerm.resetWaitingQueue();
        ioScheduler.resetIOQueue();
        memoryManager.resetMemory();
        kernel.resetClock();
        textArea.clear();
        CPU.resetRunningList();

    }

    public static void _exit() {
        System.exit(0);
    }








}