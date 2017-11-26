package User_space;


/**
 * @project OS_Simulator
 */
public class CLI extends GUI {
    public static String[] commands = {"PROC", "MEM", "EXE", "RESET", "EXIT", "LOAD"};

    public static FileParser fileParser = new FileParser();


    public static boolean isValidCommand(String input) {
        String[] command = input.toUpperCase().split(" ");
        for(String c: command) {
            System.out.println("C is : " + c);
        }
        if(command.length > 2) {
            return false;
        }
        boolean valid = false;
        for(String c: commands) {
            if(c.equalsIgnoreCase(command[0])) {
                if (command.length == 1 && !c.equals("LOAD")) {
                    valid = true;
                } else if(c.equalsIgnoreCase("LOAD")) {
                    if(command.length == 2) {
                        valid = true;
                    }
                } else {
                    System.out.println("inside else");
                    String val = command[1].replaceAll("[^0-9]","");
                    if(val.length() > 1) {
                        valid = true;
                    }
                }
            }
        }
        return valid;
    }


    public static void execute(String input) {
        String[]  command = input.toUpperCase().split(" ");
        int val = -1;
        if(command.length > 1) {
            if(command[0] != "LOAD") {
                val = Integer.parseInt(command[1]);
            }
        }
        switch(command[0]) {
            case "PROC":
                _proc();
                break;
            case "MEM":
                _mem();
                break;
            case "EXE":
                _exe(val);
                break;
            case "LOAD":
                _load(command[1]);
                break;
            case "RESET":
                _reset();
                break;
            case "EXIT":
                _exit();
                break;
            default:
                System.out.println("----INVALID COMMAND FROM CLI----");
                break;
        }
    }

    public static void _proc() {

    }

    public static void _mem() {

    }


    public static void _exe(int cycles) {
        if(cycles == -1) {
            // EXECUTE CONTINUOUSLY
        } else {
            // EXECUTE FOR THE GIVEN NUMBER OF CYCLES
        }
    }

    public static void _load(String filename) {
        fileParser.parse(filename);

    }

    public static void _reset() {

    }

    public static void _exit() {
        System.exit(0);
    }








}
