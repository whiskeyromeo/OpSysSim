package Sys.Memory;

import Sys.InterruptHandler;

import java.util.Random;

/**
 * @author Will Russell on 11/8/17
 * @project OS_Simulator
 *
 *  CPU should have multiple cores
 */
public class Core {

    private int coreId;
    private final InterruptHandler interruptHandler;

    public Core(int id) {
        this.coreId = id;
        this.interruptHandler = new InterruptHandler();
    }


}
