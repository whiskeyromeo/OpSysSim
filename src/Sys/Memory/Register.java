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
    private boolean isOccupied;

    // This should be equivalent to a register in memory
    public Register(String address) {
        this.address = address;
        this.isOccupied = false;
    }


    public static Register[] instantiateRegisterSet(int num_registers) {
        Register[] registers = new Register[num_registers];
        for(int i = 0; i  < num_registers; i++) {
            registers[i] = new Register("NULL");
        }
        return registers;
    }




    public void setData(String data) { this.data = data; }
    public void setAddress(String address) { this.address = address; }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public int size() { return REGISTER_SIZE; }
    public String getData() { return this.data; }
    public String getAddress() {
        return this.address;
    }

    public boolean isOccupied() {
        return isOccupied;
    }
}

