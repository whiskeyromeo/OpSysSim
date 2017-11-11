package Sys.Memory;

/**
 * @project OS_Simulator
 */
public class Cache {

    // Should be larger than the registers
    // Should be set by the OS(kernel)

    private int size;
    private int block_size;

    // This should be equivalent to a cache in memory
    public Cache() { }

    public int getSize() { return this.size; }
    public int getBlock_size() { return this.block_size; }

    public void setSize(int size) { this.size = size; }
    public void setBlock_size(int block_size) { this.block_size = block_size; }


}
