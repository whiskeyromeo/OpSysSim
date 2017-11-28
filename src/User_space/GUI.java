package User_space;

import Sys.*;
import Sys.Scheduling.IOScheduler;
import Sys.Scheduling.LongTerm;
import Sys.Scheduling.MultiLevel;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
import java.util.stream.Collectors;


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

    public static TableView activeTable;
    public static TableView newTable;
    public static TableView schedulerTable;

    private ScrollPane scrollPane;
    private Label label;

    static protected TextArea textArea;
    static protected TextArea schedulerTextArea;


    public static final ObservableList<PCB> activeProcessList = FXCollections.observableArrayList();
    public static final ObservableList<PCB> newProcessList = FXCollections.observableArrayList();
    private static final ObservableList<PCB> schedulerList = FXCollections.observableArrayList();


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
        window.setTitle("OS User_space.Simulator");

        TableView<PCB> processTable = new TableView<>();
        ObservableList<PCB> processList = FXCollections.observableArrayList();
            processTable.setItems(processList);

        // Ready Processes
        TableColumn pidCol = new TableColumn("PID");
            pidCol.setCellValueFactory(new PropertyValueFactory<PCB, String>("pid"));
        TableColumn parentCol = new TableColumn("PARENT");
            parentCol.setCellValueFactory(new PropertyValueFactory<PCB, String>("ppid"));
        TableColumn memAllCol = new TableColumn("MEMORY");
            memAllCol.setCellValueFactory(new PropertyValueFactory<PCB, String>("memAllocated"));
        TableColumn arrivalTimeCol = new TableColumn("ARRIVAL");
            arrivalTimeCol.setCellValueFactory(new PropertyValueFactory<PCB, String>("arrivalTime"));
        TableColumn estRunTimeCol = new TableColumn("EST RUN TIME");
            estRunTimeCol.setCellValueFactory(new PropertyValueFactory<PCB, String>("estimatedRunTime"));
        TableColumn stateCol = new TableColumn("STATE");
            stateCol.setCellValueFactory(new PropertyValueFactory<PCB, String>("currentState"));
        TableColumn commandCol = new TableColumn("EXECUTING");
            commandCol.setCellValueFactory(new PropertyValueFactory<PCB, String>("programCounter"));
            commandCol.setPrefWidth(120);


        // New Processes
        TableColumn pidCol2 = new TableColumn("PID");
            pidCol2.setCellValueFactory(new PropertyValueFactory<PCB, String>("pid"));
        TableColumn memReqCol = new TableColumn("MEMORY REQ");
            memReqCol.setCellValueFactory(new PropertyValueFactory<PCB, String>("memRequired"));
        TableColumn arrivalTimeCol2 = new TableColumn("ARRIVAL");
            arrivalTimeCol2.setCellValueFactory(new PropertyValueFactory<PCB, String>("arrivalTime"));
        TableColumn waitingCol = new TableColumn("WAITING");
            waitingCol.setCellValueFactory(new PropertyValueFactory<PCB, String>("waitTime"));
        TableColumn stateCol2 = new TableColumn("STATE");
            stateCol2.setCellValueFactory(new PropertyValueFactory<PCB, String>("currentState"));



        activeTable = new TableView();
        newTable = new TableView();
        newTable.setMaxHeight(300);

        simulator.populateReadyQueues(3);

        this.activeProcessList.setAll(multiLevel.getAllInReadyStream().collect(Collectors.toList()));
        this.newProcessList.setAll(longTermScheduler.streamWaitingQueue());


        activeTable.setItems(this.activeProcessList);
        activeTable.getColumns().addAll(pidCol, memAllCol, arrivalTimeCol, estRunTimeCol, stateCol, commandCol);

        newTable.setItems(this.newProcessList);
        newTable.getColumns().addAll(pidCol2, memReqCol, arrivalTimeCol2, waitingCol, stateCol2);


        activeTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        newTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        schedulerTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

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
        schedulerTextArea.setPrefColumnCount(29);
        schedulerTextArea.autosize();


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
        newProcessBox.getChildren().addAll(newProcessTitle, newTable);

        ScrollPane newScrollPane = new ScrollPane(newProcessBox);
        newScrollPane.setFitToHeight(true);


        VBox activeProcessBox = new VBox();
        activeProcessBox.setSpacing(10);

        Text activeProcessTitle = new Text("Active/Blocked Processes");
        activeProcessTitle.setStyle("-fx-font-size: 16px");
        activeProcessBox.getChildren().addAll(activeProcessTitle, activeTable);

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

//        this.activeProcessList.setAll(ioScheduler.getProcessesFromIOQueue().stream().collect(Collectors.toList()));
//        this.activeProcessList.setAll(cpu.getRunningList().stream().collect(Collectors.toList()));
//        this.activeProcessList.setAll(multiLevel.getAllInReadyStream().collect(Collectors.toList()));
//        this.newProcessList.setAll(longTermScheduler.streamWaitingQueue());
//
//        this.activeTable.setItems(this.activeProcessList);
//        this.newTable.setItems(this.newProcessList);

        try {
            Thread.sleep(50);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(CLI.numExeSteps == 0) {
            System.out.print(" .. ");
            InterruptHandler.interruptSignalled = true;
        }

        if((!CLI.runProgramContinuously && CLI.numExeSteps < 0) || InterruptHandler.interruptSignalled ) {
            //System.out.println("looping");
            return;
        } else if(!CLI.runProgramContinuously && CLI.numExeSteps > 0){
            CLI.numExeSteps--;
        }

        kernel.execute();
        //System.out.println("made it passed kernel");
        newTable.getItems().clear();
        activeTable.getItems().clear();



    }

    public static void updateTableValues() {
        ArrayList<PCB> activeProcesses = new ArrayList<>();
        Set<PCB> hs = new HashSet<>();
        activeProcesses.addAll(ioScheduler.getProcessesFromIOQueue());
        activeProcesses.addAll(RunningQueue.runningList);
        activeProcesses.addAll(multiLevel.getReadyQueues());
        //System.out.println("activeProcess count : " + activeProcesses.size());

        hs.addAll(activeProcesses);
        activeProcesses.clear();
        activeProcesses.addAll(hs);
        activeProcesses.sort(Comparator.comparingInt(PCB::getPid));
        schedulerTextArea.clear();


        for(PCB process : activeProcesses) {
            addSchedulerLine(process.getPCBLine());
        }


        activeProcessList.setAll(ioScheduler.getProcessesFromIOQueue().stream().collect(Collectors.toList()));
        activeProcessList.setAll(cpu.getRunningList().stream().collect(Collectors.toList()));
        activeProcessList.setAll(multiLevel.getAllInReadyStream().collect(Collectors.toList()));
        newProcessList.setAll(longTermScheduler.streamWaitingQueue());

        activeTable.setItems(activeProcessList);
        newTable.setItems(newProcessList);
    }

    public static ArrayList<String> previousCommands = new ArrayList<>();
    public static ArrayList<String> prevSchedulerCommands = new ArrayList<>();


    public static void addLine(String text) {
        textArea.appendText(text + "\n");
        previousCommands.add(text);
    }

    public static void addLine(TextArea textArea, String text){
        textArea.appendText(text + "\n");
    }

    public static void addText(String text) {
        textArea.appendText(text);
        previousCommands.add(text);
    }

    public static void addSchedulerLine(String text) {
        schedulerTextArea.appendText(text + "\n");
        prevSchedulerCommands.add(text);
    }

    public static void addSchedulerText(String text) {
        schedulerTextArea.appendText(text);
        prevSchedulerCommands.add(text);
    }

    // TODO : REMOVE WHEN READY
    public static void main(String[] args) {
        launch(args);
    }


}