package Sys.Memory;

import java.security.InvalidParameterException;

/**
 *
 *
 * @author Will Russell on 11/8/17
 * @project OS_Simulator
 */
public class MemoryManager {
        public static final int PAGE_SIZE = 4;
        private static final int TOTAL_MEMORY = 4096;
        private static int memory = TOTAL_MEMORY;
        private static MemoryManager memoryManager;


        protected MemoryManager() { }

        public static synchronized MemoryManager getInstance() {
            if(memoryManager == null) {
                memoryManager = new MemoryManager();
            }
            return memoryManager;
        }

        public static synchronized void allocateMemory(int amount) throws InvalidParameterException {
            if(amount > memory) {
                throw new InvalidParameterException("Cannot allocate more memory than available in system");
            }
            memory -= amount;
        }

        public static synchronized void deallocateMemory(int amount) {
            if(memory+amount <= TOTAL_MEMORY)
                memory += amount;
        }

        public static int getCurrentMemory() {
            return memory;
        }

        public void resetMemory() {
            memory = TOTAL_MEMORY;
        }

}
