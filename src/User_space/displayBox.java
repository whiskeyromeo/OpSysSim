package User_space;


import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.*;
import javafx.scene.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class displayBox {

    public static void display(String title, String message) {

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);
        Button close = new Button("close");
        close.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, close);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.ENTER) {
                    close.fire();
                }
            }
        });
        window.setScene(scene);
        window.showAndWait();
    }
}
