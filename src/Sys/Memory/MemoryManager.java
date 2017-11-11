package Sys.Memory;

import java.security.InvalidParameterException;

/**
 * @author Will Russell on 11/8/17
 * @project OS_Simulator
 */
public class MemoryManager {
        private static int memory = 4096;


        public static void allocateMemory(int amount) throws InvalidParameterException {
            if(amount > memory) {
                throw new InvalidParameterException("Cannot allocate more memory than available in system");
            }
            memory -= amount;
        }

        public static void deallocateMemory(int amount) {
            memory += amount;
        }

        public static int getCurrentMemory() {
            return memory;
        }


}
