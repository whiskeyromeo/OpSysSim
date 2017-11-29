package Sys;

import Sys.Memory.Register;
import User_space.GUI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Capitan on 11/7/17
 * @project OS_Simulator
 *
 *  Each CPU should be implemented with a thread
 *
 */
public class CPU {


    public static final int REGISTER_COUNT = 64;
    public static final int CORE_COUNT = 4;

    public static final int CACHE_SIZE = 120;
    public static ArrayList<Thread> threadCores = new ArrayList<>();
    public static ArrayList<Core> cores = new ArrayList<>();
    public static boolean coresInitialized = false;
    public static Semaphore semaphore;

    public static int totalCoreCycles = 0;
    public static int totalCyclesPermitted = totalCoreCycles;
    public static double avgCycleCount = totalCoreCycles/CORE_COUNT;



    public static BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private static CopyOnWriteArrayList<PCB> runningProcesses = new CopyOnWriteArrayList<>();

    private Register[] registerSet;     // The amount of memory available to a CPU
    private int cpuID;      // Figure we need an id to keep track of each CPU in case of multiple cores


    public CPU(int cpuId) {
        this.registerSet = Register.instantiateRegisterSet(REGISTER_COUNT);
        this.cpuID = cpuId;
        this.semaphore = new Semaphore(1);
    }

    /**
     * Initialize a thread with a Core object for each in the CORE_COUNT
     */
    public void initializeThreadCores() {
        for(int i = 0; i < CORE_COUNT; i++) {
            Thread core = new Thread(new Core(semaphore, messageQueue, i));
            threadCores.add(core);
        }
        coresInitialized = true;
    }

    /**
     *  Start up each thread --> only can be used without GUI
     */
    public void startThreadCores() {
        for(Thread core: threadCores) {
            core.start();
        }
    }


    /**
     *  Run each thread --> Can be used with the GUI as is technically
     *  still single threaded
     */
    public void runThreadCores() {
        for(Thread c:threadCores) {
            c.run();
        }
    }

    /**
     * Cycle the cpu cores;
     */
    public void run() {
        if(GUI.isActive) {
            // Run using simulated cores if GUI is being used
            if(!coresInitialized) {
                initializeThreadCores();
            }
            runThreadCores();
        } else {
            if(!coresInitialized) {
                // Otherwise multithread
                initializeThreadCores();
                startThreadCores();
            }
        }

    }

    public Stream<PCB> getRunningStream() {
        return runningProcesses.stream();
    }

    public List<PCB> streamRunningList() {
        return runningProcesses.stream().collect(Collectors.toList());
    }

    public ArrayList<PCB> getRunningList() {
        return RunningQueue.runningList;
    }

    public int getRunningCount() {
        return RunningQueue.getSize();
    }

    public static void advanceTotalCycles() {
        totalCoreCycles++;
    }

    public static int getTotalCycles() {
        return totalCoreCycles;
    }

    public static void resetRunningList() {
        runningProcesses.clear();
    }

    public static void resetQualities() {
        runningProcesses.clear();
        totalCoreCycles = 0;
    }
}
