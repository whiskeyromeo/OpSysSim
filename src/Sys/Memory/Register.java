package Sys.Memory;

/**
 * @project OS_Simulator
 */
public class Register {
    // Should be the smallest element of memory larger than an address
    // Should be defined by the CPU;

    private final int REGISTER_SIZE = 4;    // 32 bit architecture

    private int address;
    private int data;

    // This should be equivalent to a register in memory
    public Register(int address) {
        this.address = address;
    }


    public void setData(int data) { this.data = data; }
    public void setAddress(int address) { this.address = address; }

    public int size() { return REGISTER_SIZE; }
    public int getData() { return this.data; }
    public int getAddress() {
        return this.address;
    }

}

