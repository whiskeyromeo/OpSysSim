package Sys.Memory;

import Sys.PCB;

import java.security.InvalidParameterException;


/**
 * Only one copy of main memory --> Singleton
 * @project OS_Simulator
 */
public class MainMemory {

    private static MainMemory ram;
    private static final int TOTAL_MEMORY = 512;
    private volatile static int memoryAllocated = 0;
    private volatile static int memoryFree = (TOTAL_MEMORY - memoryAllocated);

    // Need some way to store items in main memory -->
    // When a process is loaded for execution it should go into main memory
    //

    protected MainMemory() { }

    public static MainMemory getInstance() {
        if(ram == null) {
            ram = new MainMemory();
        }
        return ram;
    }

    public int getFreeMemory() {
        return memoryFree;
    }

    public void allocateMemory(int amount) throws InvalidParameterException {
        if(amount > memoryFree) {
            throw new InvalidParameterException("Cannot allocate more memory than exists");
        }
    }






}
