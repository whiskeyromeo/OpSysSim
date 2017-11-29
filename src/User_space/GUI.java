package User_space;

import Sys.*;
import Sys.Scheduling.IOScheduler;
import Sys.Scheduling.LongTerm;
import Sys.Scheduling.MultiLevel;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Will Russell on 11/8/17
 * @project OS_Simulator
 */
public class GUI extends Application {

    private Stage window;
    private BorderPane root;
    private VBox bottomBox;
    private HBox controls;

    private javafx.scene.control.TextField textInput;
    private javafx.scene.control.Button button;

    private ScrollPane scrollPane;
    private Label label;

    static protected TextArea textArea;
    static protected TextArea schedulerTextArea;
    static protected TextArea activeProcessesTextArea;
    static protected TextArea newProcessesTextArea;

    public static boolean isActive = false;

    ///************ SYSTEM INSTANCES HERE ******************* //

    static MultiLevel multiLevel = MultiLevel.getInstance();
    static Simulator simulator = Simulator.getInstance();
    static LongTerm longTermScheduler = LongTerm.getInstance();
    static IOScheduler ioScheduler = IOScheduler.getInstance();
    static Kernel kernel = Kernel.getInstance();

    static CPU cpu = new CPU(1);


    //***************END SYSTEM INSTANCES ***************** //

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("OS Simulator");


        simulator.populateReadyQueues(3);


        textInput = new TextField();
        textInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
                button.fire();
            }
        }
        });


        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setFocusTraversable(false);
        textArea.setPrefRowCount(10);
        textArea.setScrollTop(0);
        textArea.setPrefColumnCount(50);
        textArea.autosize();

        schedulerTextArea = new TextArea();
        schedulerTextArea.setEditable(false);
        schedulerTextArea.setFocusTraversable(false);
        schedulerTextArea.setPrefRowCount(20);
        schedulerTextArea.setScrollTop(0);
        schedulerTextArea.setPrefColumnCount(25);
        schedulerTextArea.autosize();

        activeProcessesTextArea = new TextArea();
        activeProcessesTextArea.setEditable(false);
        activeProcessesTextArea.setFocusTraversable(true);
        activeProcessesTextArea.setPrefRowCount(17);
        activeProcessesTextArea.setScrollTop(0);
        activeProcessesTextArea.setPrefColumnCount(40);
        activeProcessesTextArea.autosize();

        newProcessesTextArea = new TextArea();
        newProcessesTextArea.setEditable(false);
        newProcessesTextArea.setFocusTraversable(false);
        newProcessesTextArea.setPrefRowCount(11);
        //newProcessesTextArea.setScrollTop(0);
        newProcessesTextArea.setPrefColumnCount(40);
        newProcessesTextArea.autosize();


        button = new Button();
        button.setText("Submit");
        button.setOnAction(event -> {
            boolean valid = false;
            String inputText = textInput.getText();
            if (!inputText.trim().equals(""))
                valid = CLI.isValidCommand(textInput.getText());
            textInput.clear();
            if (!valid) {
//                displayBox.display("ERROR", "INVALID COMMAND");
                textArea.setText(inputText + " is not a valid command");
            } else {
                CLI.execute(inputText);
            }
        });

        controls = new HBox();
        controls.setSpacing(10);
        controls.getChildren().addAll(textInput, button);


        root = new BorderPane();

        VBox newProcessBox = new VBox();
        newProcessBox.setSpacing(10);

        Text newProcessTitle = new Text("New Process Queue");
        newProcessTitle.setStyle("-fx-font-size: 16px");
        newProcessBox.getChildren().addAll(newProcessTitle, newProcessesTextArea);

        ScrollPane newScrollPane = new ScrollPane(newProcessBox);
        newScrollPane.setFitToHeight(true);


        VBox activeProcessBox = new VBox();
        activeProcessBox.setSpacing(10);

        Text activeProcessTitle = new Text("Active/Blocked Processes");
        activeProcessTitle.setStyle("-fx-font-size: 16px");
        activeProcessBox.getChildren().addAll(activeProcessTitle, activeProcessesTextArea);

        ScrollPane activeScrollPane = new ScrollPane(activeProcessBox);
        activeScrollPane.setFitToHeight(true);

        VBox topRightBox = new VBox();
        topRightBox.setSpacing(10);

        Text schedulerTableTitle = new Text("Scheduler Stats");
        schedulerTableTitle.setStyle("-fx-font-size: 16px");

        topRightBox.getChildren().addAll(schedulerTableTitle, schedulerTextArea);


        VBox topLeftBox = new VBox();
        topLeftBox.setSpacing(10);
        topLeftBox.getChildren().addAll(newProcessBox, activeProcessBox);
        topLeftBox.setPadding(new Insets(8));


        HBox topBox = new HBox();
        topBox.setSpacing(10);
        topBox.getChildren().addAll(topLeftBox, topRightBox);
        topBox.setPadding(new Insets(8));

        scrollPane = new ScrollPane(topBox);
        scrollPane.setFitToHeight(true);

        root = new BorderPane(scrollPane);
        root.setPadding(new Insets(15));


        bottomBox = new VBox();
        bottomBox.setSpacing(10);
        bottomBox.setPadding(new Insets(10, 10, 10, 10));
        bottomBox.getChildren().addAll(textArea, controls);

        root.setTop(topBox);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1000, 600);
        window.setScene(scene);
        updateTableValues();

        isActive = true;
        runSim();
    }



    public void runSim() throws InterruptedException {
       // this.activeProcessList.setAll(multiLevel.getAllInReadyStream().collect(Collectors.toList()));
//        this.newProcessList.setAll(longTermScheduler.streamWaitingQueue());

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    loopMethod();

                } catch(InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }.start();

        window.show();
    }

    public void loopMethod() throws InterruptedException {


        try {
            Thread.sleep(50);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(CLI.numExeSteps == 0) {
            InterruptHandler.interruptSignalled = true;
        }

        updateTableValues();

        if((!CLI.runProgramContinuously && CLI.numExeSteps < 0) || InterruptHandler.interruptSignalled ) {
            //System.out.println("looping");
            return;
        } else if(!CLI.runProgramContinuously && CLI.numExeSteps > 0){
            CLI.numExeSteps--;
        }

        kernel.execute();

    }


    public static synchronized void updateTableValues() {
        // **** update active processes ******
        ArrayList<PCB> activeProcesses = new ArrayList<>();
        Set<PCB> hs = new HashSet<>();
        activeProcesses.addAll(ioScheduler.getProcessesFromIOQueue());
        activeProcesses.addAll(RunningQueue.runningList);
        activeProcesses.addAll(multiLevel.getReadyQueues());

        hs.addAll(activeProcesses);
        activeProcesses.clear();
        activeProcesses.addAll(hs);
        activeProcesses.sort(Comparator.comparingInt(PCB::getPid));



        activeProcessesTextArea.clear();
        newProcessesTextArea.clear();

        for(PCB process : activeProcesses) {
            addLine(activeProcessesTextArea, process.getPCBLine());
        }

        // **** end update active processes ******

        // ***** update new processes ********
        ArrayList<PCB> newQueue = longTermScheduler.getWaitingQueue();
        for(PCB process : newQueue) {
            addLine(newProcessesTextArea, process.getNewPCBLine());
        }

    }

    public static ArrayList<String> previousCommands = new ArrayList<>();
    public static ArrayList<String> prevSchedulerCommands = new ArrayList<>();


    public static synchronized void addLine(String text) {
        textArea.appendText(text + "\n");
        previousCommands.add(text);
    }

    public static synchronized void addLine(TextArea textArea, String text){
        textArea.appendText(text + "\n");
    }

    public static synchronized void addText(String text) {
        textArea.appendText(text);
        previousCommands.add(text);
    }

    public static synchronized void addSchedulerLine(String text) {
        schedulerTextArea.appendText(text + "\n");
        prevSchedulerCommands.add(text);
    }

    public static synchronized void addSchedulerText(String text) {
        schedulerTextArea.appendText(text);
        prevSchedulerCommands.add(text);
    }

    // TODO : REMOVE WHEN READY
    public static void main(String[] args) {
        launch(args);
    }


}