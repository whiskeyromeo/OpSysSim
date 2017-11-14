package Sys.Memory;

/**
 * @project OS_Simulator
 */
public class Register {
    // Should be the smallest element of memory larger than an address
    // Should be defined by the CPU;

    public static final int REGISTER_SIZE = 4;    // 32 bit architecture

    private String address;
    private String data;

    // This should be equivalent to a register in memory
    public Register(String address) {
        this.address = address;
    }


    public void setData(String data) { this.data = data; }
    public void setAddress(String address) { this.address = address; }

    public int size() { return REGISTER_SIZE; }
    public String getData() { return this.data; }
    public String getAddress() {
        return this.address;
    }

}

