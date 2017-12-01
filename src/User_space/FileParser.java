package User_space;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    /**
     * Parses a file in order to get the commands
     * @param fileName
     */
    public void parse(String fileName) {

        input = "./src/Program_Files/" + fileName;

        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current relative path is: " + s + input);
        // Make sure the queue is clear
        commandQueue.clear();

        try {
            File file = new File(input);
            scanner = new Scanner(file);
            while(scanner.hasNextLine()) {
                String command = scanner.nextLine();
                commandQueue.add(command);
            }
        } catch(Throwable e) {
            System.out.println("COULD NOT FIND FILE");
            return;
        }
        scanner.close();

    }


    public ArrayList<String> getCommandQueue() {
        return commandQueue;
    }



}
