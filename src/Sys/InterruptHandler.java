package Sys;

/**
 * @project OS_Simulator
 */
public class InterruptHandler {

    public static boolean interruptSignalled = false;
    public static boolean deviceInterruptSignalled = false;


    public static void signalInterrupt() {
        interruptSignalled = true;
    }

    public static void signalDeviceInterrupt() {
        deviceInterruptSignalled = true;
    }

}
