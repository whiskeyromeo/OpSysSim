import Sys.Kernel;
import Sys.PCB;

/**
 * @author Capitan on 11/7/17
 * @project OS_Simulator
 */
public class Simulator {

    public static void main(String[] args) {

        // Fire up the kernel
        Kernel kernel = new Kernel();

        // Check that the base theory works
        System.out.println("check");
        System.out.println("System clock : " + kernel.getSystemClock());
        kernel.advanceClock();
        System.out.println("System clock : " + kernel.getSystemClock());
        PCB firstProcess = new PCB(kernel.getNewPid(), -1);

        System.out.println("Process " + firstProcess.getPid()  +  " arrival : " + firstProcess.getArrivalTime());

        kernel.advanceClock();
        kernel.advanceClock();

        PCB secondProcess = new PCB(kernel.getNewPid(), -1);

        System.out.println("Process " + secondProcess.getPid()  +  " arrival : " + secondProcess.getArrivalTime());
        PCB firstChild = firstProcess._fork();

        System.out.format("First child : %d, parent: %d, arrival : %d, currentStatus : %s",
                firstChild.getPpid(), firstChild.getPpid(), firstChild.getArrivalTime(), firstChild.getCurrentState().toString());






    }

}
