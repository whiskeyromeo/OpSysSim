package Sys;

import Sys.Memory.MemoryManager;
import Sys.Memory.Register;
import Sys.Scheduling.MultiLevel;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

/**
 * @author Capitan on 11/7/17
 * @project OS_Simulator
 *
 *  Each CPU should be implemented with a thread
 *
 */
public class CPU implements Runnable {


    public static final int REGISTER_COUNT = 64;
    public static final int CORE_COUNT = 4;
    public static final int CACHE_SIZE = 120;
    public static ArrayList<Core> cores;
    public static Semaphore semaphore;
    public static BlockingQueue<String> messageQueue;

    private Register[] registerSet;     // The amount of memory available to a CPU
    private int cpuID;      // Figure we need an id to keep track of each CPU in case of multiple cores
    private int clock;
    private boolean terminationStatus;

    public CPU(int cpuId) {
        this.registerSet = Register.instantiateRegisterSet(REGISTER_COUNT);
        this.cpuID = cpuId;
        this.clock = 0;
        this.cores = generateCores(0,CORE_COUNT);
        this.terminationStatus = false;
        this.semaphore = new Semaphore(1);
        this.messageQueue = new LinkedBlockingDeque<>();
    }

    @Override
    public void run() {
        for(Core core: cores) {
            Thread t = new Thread(core);
            t.start();
        }

    }

    private ArrayList<Core> generateCores(int baseId, int num_cores) {
        cores = new ArrayList<>();
        for(int i = 0; i < num_cores; i++) {
            cores.add(new Core(this.semaphore, this.messageQueue,baseId+(i+1)));
        }
        return cores;
    }



    public boolean checkTerminationStatus() {
        return this.terminationStatus;
    }

    public void setTerminationStatus(boolean val) {
        this.terminationStatus = true;
    }



}
