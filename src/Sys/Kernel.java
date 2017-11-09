package Sys;

/**
 * @author Will Russell on 11/8/17
 * @project OS_Simulator
 */
public class Kernel {

    private CPU cpu;
    private int processCounter;     // Should be used to assign the pid for each process --> Maybe should be volatile to be threadsafe?


    public Kernel(int id) {
        this.cpu = new CPU();
        this.processCounter = 0;
    }


    public int getProcessCounter() {
        return this.processCounter;
    }

    public void setProcessCounter() {
        this.processCounter++;
    }






}
