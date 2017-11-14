package Sys.Memory;

import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 *
 *
 * @author Will Russell on 11/8/17
 * @project OS_Simulator
 */
public class MemoryManager {
        private static int memory = 4096;
        private static final int TOTAL_MEMORY = 4096;
        private static MemoryManager memoryManager;

        private static ArrayList<Register> registers;



        protected MemoryManager() {
            createRegisters();  //generate the registers that will be used

        }

        public static MemoryManager getInstance() {
            if(memoryManager == null) {
                memoryManager = new MemoryManager();
            }
            return memoryManager;
        }


        public static void createRegisters() {
            String addressString = "reg00";
            for(int i=0; i < 16; i++) {
                String regId = String.valueOf(i);
                registers.add(new Register(addressString + regId));
                allocateMemory(Register.REGISTER_SIZE);
            }
        }

        public static void createCaches() {
            return;
        }




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
