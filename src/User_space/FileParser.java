package User_space;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @project OS_Simulator
 */
public class FileParser {

    private Scanner scanner;
    private String input;
    private String command;
    private ArrayList<String> commandQueue;


    public FileParser() {
        this.commandQueue = new ArrayList<>();
    }

    public void parse(String fileName) {
        input = "Program_Files/" + fileName + ".job";

        // Make sure the queue is clear
        commandQueue.clear();

        try {
            File file = new File(input);
            scanner = new Scanner(file);
            while(scanner.hasNext()) {
                commandQueue.add(scanner.next());
            }
        } catch(Exception e) {
            System.out.println("COULD NOT FIND FILE");
        }
        scanner.close();

    }

    public ArrayList<String> getCommandQueue() {
        return commandQueue;
    }



}
