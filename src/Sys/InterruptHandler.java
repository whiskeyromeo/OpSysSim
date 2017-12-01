package Sys;

import java.util.Random;

/**
 * @project OS_Simulator
 */
public class InterruptHandler {

    public static Random r = new Random();
    public static boolean interruptSignalled = false;
    public static boolean deviceInterruptSignalled = false;
    public static int deviceTime;
    public static boolean isBeingServiced = false;
    public static int serviceID = -1;

    public synchronized static void signalInterrupt() {
        interruptSignalled = true;
    }


    /**
     * Generates a random device-based interrupt
     * @param coreId the core which the interrupt should be serviced by
     */
    public synchronized static void checkForDeviceInterrupt(int coreId) {
        if(deviceInterruptSignalled) {
            return;
        }
        deviceTime = r.nextInt(20) + 1;
        if(21 % deviceTime  == 0) {
            //System.out.println("Setting device interrupt");
            deviceInterruptSignalled = true;
            serviceID = coreId;
        } else {
            deviceInterruptSignalled = false;
        }
    }


    /**
     * Service an interrupt which has occurred
     */
    public synchronized static void serviceDeviceInterrupt() {

        if(deviceTime > 0) {
            //System.out.println("Decrementing device time");
            deviceTime--;
        } else {
            //System.out.println("Resetting device interrupt");
            serviceID = -1;
            deviceInterruptSignalled = false;
        }
    }



}
