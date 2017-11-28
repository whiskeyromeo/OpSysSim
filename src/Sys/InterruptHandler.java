package Sys;

/**
 * @project OS_Simulator
 */
public class InterruptHandler {

    public static boolean interruptSignalled = false;

    public static void signalInterrupt() {
        interruptSignalled = true;
    }

}
