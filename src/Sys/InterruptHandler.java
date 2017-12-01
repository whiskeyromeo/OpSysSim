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

    public static void signalInterrupt() {
        interruptSignalled = true;
    }


    public static void checkForDeviceInterrupt(int coreId) {
        if(deviceInterruptSignalled) {
            return;
        }
        deviceTime = r.nextInt(20) + 1;
        if(deviceTime % 6 == 0) {
            System.out.println("Setting device interrupt");
            deviceInterruptSignalled = true;
            serviceID = coreId;
        } else {
            deviceInterruptSignalled = false;
        }
    }



    public static void serviceDeviceInterrupt() {

        if(deviceTime > 0) {
            //System.out.println("Decrementing device time");
            deviceTime--;
        } else {
            System.out.println("Resetting device interrupt");
            serviceID = -1;
            deviceInterruptSignalled = false;
        }
    }



}
