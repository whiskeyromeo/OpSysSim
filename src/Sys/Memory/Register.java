package Sys.Memory;

/**
 * @project OS_Simulator
 */
public class Register {
    // Should be the smallest element of memory larger than an address
    // Should be defined by the CPU;

    private int address;
    private int size;


    // This should be equivalent to a register in memory
    public Register(int size) { this.size = size; }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getAddress() {
        return address;
    }
}

