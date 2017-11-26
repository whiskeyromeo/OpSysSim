package User_space;

import Sys.PCB;
import Sys.Scheduling.LongTerm;
import Sys.Scheduling.MultiLevel;
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

    private TableView activeTable;
    private TableView newTable;
    private TableView schedulerTable;

    private ScrollPane scrollPane;
    private Label label;

    static protected TextArea textArea;

    private final ObservableList<PCB> activeProcessList = FXCollections.observableArrayList();
    private final ObservableList<PCB> newProcessList = FXCollections.observableArrayList();
    private final ObservableList<PCB> schedulerList = FXCollections.observableArrayList();


    ///************ SYSTEM INSTANCES HERE ******************* //

    MultiLevel multiLevel = MultiLevel.getInstance();
    Simulator simulator = Simulator.getInstance();
    LongTerm longTermScheduler = LongTerm.getInstance();


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


        simulator.populateReadyQueues(10, 500);

        this.activeProcessList.setAll(multiLevel.getAllInReady());
        this.newProcessList.setAll(longTermScheduler.getWaitingQueue());

        // Scheduler Table
        TableColumn schedCol = new TableColumn("SCHEDULER");
            schedCol.setCellValueFactory(new PropertyValueFactory<PCB, String>("scheduler"));
        TableColumn throughputCol = new TableColumn("THROUGHPUT");
            throughputCol.setCellValueFactory(new PropertyValueFactory<PCB, String>("throughput"));
        TableColumn avgWaitCol = new TableColumn("AVG WAITING");
            avgWaitCol.setCellValueFactory(new PropertyValueFactory<PCB, String>("avg_wait"));
        TableColumn avgResponseCol = new TableColumn("AVG RESPONSE");
            avgResponseCol.setCellValueFactory(new PropertyValueFactory<PCB, String>("avg_response"));


        schedulerTable = new TableView();
        activeTable = new TableView();
        newTable = new TableView();
        newTable.setMaxHeight(300);

        activeTable.setItems(this.activeProcessList);
        activeTable.getColumns().addAll(pidCol, memAllCol, arrivalTimeCol, estRunTimeCol, stateCol, commandCol);

        newTable.setItems(this.newProcessList);
        newTable.getColumns().addAll(pidCol2, memReqCol, arrivalTimeCol2, waitingCol, stateCol2);

        schedulerTable.setItems(this.schedulerList);
        schedulerTable.getColumns().addAll(schedCol, throughputCol, avgWaitCol, avgResponseCol);

        activeTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        newTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        schedulerTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        textInput = new TextField();
            textInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    button.fire();
                } else {
                    System.out.println("key event : " + event.getCode());
                }
            }
        });


        textArea = new TextArea();
            textArea.setEditable(false);
            textArea.setFocusTraversable(false);
            textArea.setPrefRowCount(3);
            textArea.setScrollTop(0);
            textArea.setPrefColumnCount(50);
            textArea.autosize();


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
                textArea.setText("Executing Command : \"" + inputText + "\"");
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

        topRightBox.getChildren().addAll(schedulerTableTitle, schedulerTable);


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

            window.show();

        //startup();
    }

//    public void startup() throws InterruptedException {
//        //this.activeProcessList.setAll(Scheduler.getReadyQueue().stream().collect(Collectors.toList()));
//        //this.waitingProcessList.setAll(Scheduler.getWaitingQueue().stream().collect(Collectors.toList()));
//
//        final long[] prevTime = {0};
//
//        new AnimationTimer() {
//            @Override public void handle(long currentNanoTime) {
//                if (currentNanoTime > prevTime[0] + 90000000) {
////                    try {
////                        loop();
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
//
//                    prevTime[0] = currentNanoTime + 90000000;
//                }
//            }
//        }.start();
//    }

    public void loop() throws InterruptedException {
//        // Render GUI
//        this.activeProcessList.setAll(Scheduler.getReadyQueue().stream().collect(Collectors.toList()));
//        this.waitingProcessList.setAll(Scheduler.getWaitingQueue().stream().collect(Collectors.toList()));
//
//        // Run User_space.Simulator
//        if (!User_space.Simulator.exeContinuously && User_space.Simulator.exeSteps == 0) {
//            return;
//        } else {
//            User_space.Simulator.exeSteps--;
//        }
//
//        os.execute();
    }

    // TODO : REMOVE WHEN READY
    public static void main(String[] args) {

        launch(args);
    }


}