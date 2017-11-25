package Sys;

import java.util.Random;

/**
 * @project OS_Simulator
 */
public class IOBurst {
    public static final int UPPER_BOUND = 50;
    public static final int LOWER_BOUND = 25;


    public static int generateIO() {
        Random random = new Random();
        return LOWER_BOUND + random.nextInt(LOWER_BOUND);
    }


}
