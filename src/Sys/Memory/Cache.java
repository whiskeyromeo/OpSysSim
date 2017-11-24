package Sys.Memory;

/**
 * @project OS_Simulator
 */
public class Cache {

    // Should be larger than the registers
    // Should be created with the CPU

    int [][] cache;
    boolean[][] mod;
    boolean[] valid;
    boolean changed;


    public Cache(int cache_size) {
        cache = new int[cache_size][MemoryManager.PAGE_SIZE];
        mod = new boolean[cache_size][MemoryManager.PAGE_SIZE];
        valid = new boolean[cache_size];
    }
}
